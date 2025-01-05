package ru.aleksandrchistov.budget.pages.report;

import lombok.experimental.UtilityClass;
import ru.aleksandrchistov.budget.pages.budget.model.BudgetMonth;
import ru.aleksandrchistov.budget.pages.report.dto.TotalMonthDto;
import ru.aleksandrchistov.budget.pages.transaction.Transaction;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ReportUtility {

    public static Map<Integer, BigDecimal[]> getTransactionMonths(List<Transaction> transactions) {
        Map<Integer, BigDecimal[]> transactionMonths = new HashMap<>();
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

    public static Map<Integer, BigDecimal[]> getPlanMonths(List<BudgetMonth> plans) {
        Map<Integer, BigDecimal[]> planMonths = new HashMap<>();
        for (BudgetMonth plan : plans) {
            if (plan.getIndex() < 12) {
                if (planMonths.containsKey(plan.getBudgetItem().getId())) {
                    BigDecimal[] months = planMonths.get(plan.getBudgetItem().getId());
                    BigDecimal monthSum = months[plan.getIndex()];
                    months[plan.getIndex()] = monthSum != null ? monthSum.add(plan.getSum()) : plan.getSum();
                } else {
                    BigDecimal[] months = new BigDecimal[12];
                    months[plan.getIndex()] = plan.getSum();
                    planMonths.put(plan.getBudgetItem().getId(), months);
                }
            }
        }
        return planMonths;
    }

    public static TotalMonthDto getTotalDto(String title, BigDecimal[] plans, BigDecimal[] actuals) {
        BigDecimal plan = Arrays.stream(plans).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal actual = Arrays.stream(actuals).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new TotalMonthDto(title, plan, actual);
    }

}
