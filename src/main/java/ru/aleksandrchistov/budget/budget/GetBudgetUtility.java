package ru.aleksandrchistov.budget.budget;

import lombok.experimental.UtilityClass;
import ru.aleksandrchistov.budget.budget.dto.BudgetDataDto;
import ru.aleksandrchistov.budget.budget.dto.BudgetDataMonthDto;
import ru.aleksandrchistov.budget.budget.dto.BudgetDto;
import ru.aleksandrchistov.budget.budget.dto.BudgetItemDto;
import ru.aleksandrchistov.budget.budget_item.BudgetItem;
import ru.aleksandrchistov.budget.shared.model.BudgetType;
import ru.aleksandrchistov.budget.transaction.Transaction;
import ru.aleksandrchistov.budget.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.*;

@UtilityClass
public class GetBudgetUtility {

    public static HashMap<Integer, BigDecimal[]> getTransactionMonths(List<Transaction> transactions) {
        HashMap<Integer, BigDecimal[]> transactionMonths = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (transactionMonths.containsKey(transaction.getBudgetItem().getId())) {
                BigDecimal[] trMonths = transactionMonths.get(transaction.getBudgetItem().getId());
                BigDecimal monthSum = trMonths[transaction.getPaymentDate().getMonthValue() - 1];
                trMonths[transaction.getPaymentDate().getMonthValue() - 1] = monthSum != null ? monthSum.add(transaction.getSum()) : transaction.getSum();
            } else {
                BigDecimal[] trMonths = new BigDecimal[12];
                trMonths[transaction.getPaymentDate().getMonthValue() - 1] = transaction.getSum();
                transactionMonths.put(transaction.getBudgetItem().getId(), trMonths);
            }
        }
        return transactionMonths;
    }

    public static HashMap<Integer, BudgetDataDto> getPlanDtos(List<BudgetMonth> plans, HashMap<Integer, BigDecimal[]> transactionMonths) {
        HashMap<Integer, BudgetDataMonthDto[]> planMonths = new HashMap<>();
        HashMap<Integer, BudgetDataDto> planTotals = new HashMap<>();
        for (BudgetMonth budgetMonth : plans) {
            if (budgetMonth.getIndex() < 12) {
                BigDecimal[] actualMonths = transactionMonths.get(budgetMonth.getBudgetItem().getId());
                BigDecimal actualMonthSum = actualMonths != null ? actualMonths[budgetMonth.getIndex()] : BigDecimal.valueOf(0);
                BudgetDataMonthDto dataMonthDto = new BudgetDataMonthDto(
                        budgetMonth.getId(),
                        budgetMonth.getIndex(),
                        actualMonthSum != null ? actualMonthSum : BigDecimal.valueOf(0),
                        budgetMonth.getSum(),
                        budgetMonth.getBudgetItem().getId()
                );
                if (planMonths.containsKey(budgetMonth.getBudgetItem().getId())) {
                    BudgetDataMonthDto[] months = planMonths.get(budgetMonth.getBudgetItem().getId());
                    months[budgetMonth.getIndex()] = dataMonthDto;
                }
            } else {
                BigDecimal[] actualMonths = transactionMonths.get(budgetMonth.getBudgetItem().getId());
                BigDecimal total2;
                if (actualMonths != null) {
                    total2 = Arrays.stream(actualMonths).reduce(BigDecimal.valueOf(0), (m1, m2) -> m1.add(m2 != null ? m2 : BigDecimal.valueOf(0)));
                } else {
                    total2 = BigDecimal.valueOf(0);
                }
                BudgetDataMonthDto[] months = new BudgetDataMonthDto[12];
                planMonths.put(budgetMonth.getBudgetItem().getId(), months);
                BudgetDataDto dataDto = new BudgetDataDto(
                        budgetMonth.getId(),
                        budgetMonth.getBudgetItem().getName(),
                        total2,
                        budgetMonth.getSum(),
                        months
                );
                planTotals.put(budgetMonth.getBudgetItem().getId(), dataDto);
            }
        }
        return planTotals;
    }

    public static List<BudgetItemDto> getItemDtos(List<BudgetItem> items, HashMap<Integer, BudgetDataDto> planTotals) {
        Map<Integer, BudgetItemDto> itemMap = new HashMap<>();
        List<BudgetItemDto> rootItems = new ArrayList<>();

        for (BudgetItem item : items) {
            BudgetDataDto data = planTotals.get(item.id());
            itemMap.put(item.getId(), new BudgetItemDto(data, item.getTransactionType()));
        }

        for (BudgetItem item : items) {
            if (item.getParentId() == null) {
                BudgetItemDto itemDto = itemMap.get(item.getId());
                rootItems.add(itemDto);
            } else {
                if (itemMap.containsKey(item.getParentId())) {
                    BudgetItemDto parent = itemMap.get(item.getParentId());
                    BudgetItemDto child = itemMap.get(item.getId());
                    parent.getData().setActualTotal(parent.getData().getActualTotal().add(child.getData().getActualTotal()));
                    parent.getData().setPlanTotal(parent.getData().getPlanTotal().add(child.getData().getPlanTotal()));
                    BudgetDataMonthDto[] parentDataMonths = parent.getData().getMonths();
                    BudgetDataMonthDto[] childDataMonths = child.getData().getMonths();
                    for (int i = 0; i < parentDataMonths.length; i++) {
                        parentDataMonths[i].setActual(
                                parentDataMonths[i].getActual().add(childDataMonths[i].getActual())
                        );
                    }
                    parent.addChild(child);
                }
            }
        }

        return rootItems;
    }

    public static BudgetDto budgetDto(List<BudgetItemDto> items, BudgetType budgetType) {
        BudgetDataDto totals = new BudgetDataDto();
        totals.setName("Итого " + budgetType.getText().toLowerCase());
        totals.setActualTotal(BigDecimal.valueOf(0));
        totals.setPlanTotal(BigDecimal.valueOf(0));
        BudgetDataMonthDto[] totalMonths = new BudgetDataMonthDto[12];

        for (BudgetItemDto item : items) {
            BudgetDataDto data = item.getData();
            BigDecimal actualTotal = getSum(totals.getActualTotal(), getSignedNumber(data.getActualTotal(), item.getType(), budgetType));
            BigDecimal planTotal = getSum(totals.getPlanTotal(), getSignedNumber(data.getPlanTotal(), item.getType(), budgetType));
            totals.setActualTotal(actualTotal);
            totals.setPlanTotal(planTotal);

            BudgetDataMonthDto[] months = data.getMonths();

            for (int i = 0; i < months.length; i++) {
                BudgetDataMonthDto month = new BudgetDataMonthDto();
                month.setIndex(months[i].getIndex());
                month.setActual(getSignedNumber(months[i].getActual(), item.getType(), budgetType));
                month.setPlan(getSignedNumber(months[i].getPlan(), item.getType(), budgetType));
                month.setBudgetItemId(months[i].getBudgetItemId());

                if (totalMonths[i] == null) {
                    totalMonths[i] = month;
                } else {
                    BigDecimal actual = getSum(totalMonths[i].getActual(), month.getActual());
                    BigDecimal plan = getSum(totalMonths[i].getPlan(), month.getPlan());
                    totalMonths[i].setActual(actual);
                    totalMonths[i].setPlan(plan);
                }
            }
        }

        totals.setMonths(totalMonths);

        Collections.sort(items);

        return new BudgetDto(items, totals, budgetType);
    }

    private static BigDecimal getSum(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }

    private static BigDecimal getSignedNumber(BigDecimal sum, TransactionType type, BudgetType budgetType) {
        if (budgetType == BudgetType.EXPENSES) {
            return sum;
        }
        return type == TransactionType.EXPENSE ? sum.negate() : sum;
    }

}
