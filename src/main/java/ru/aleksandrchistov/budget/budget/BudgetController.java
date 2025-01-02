package ru.aleksandrchistov.budget.budget;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.aleksandrchistov.budget.budget.dto.BudgetDataDto;
import ru.aleksandrchistov.budget.budget.dto.BudgetDataMonthDto;
import ru.aleksandrchistov.budget.budget.dto.BudgetDto;
import ru.aleksandrchistov.budget.budget.dto.BudgetItemDto;
import ru.aleksandrchistov.budget.budget_item.BudgetItem;
import ru.aleksandrchistov.budget.budget_item.BudgetItemRepository;
import ru.aleksandrchistov.budget.shared.model.BudgetType;
import ru.aleksandrchistov.budget.transaction.Transaction;
import ru.aleksandrchistov.budget.transaction.TransactionRepository;

import java.math.BigDecimal;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
@RequestMapping(value = BudgetController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class BudgetController {

    static final String REST_URL = "/api/budgets";
    protected final Logger log = getLogger(getClass());

    @Autowired
    private BudgetRepository repository;

    @Autowired
    private BudgetPlanRepository planRepository;

    @Autowired
    private BudgetItemRepository itemRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping(path = "/names")
    public List<Budget> getNames(@RequestParam BudgetType type) {
        log.info("getNames");
        return repository.getAllByType(type);
    }

    @GetMapping
    public BudgetDto getAll(@RequestParam BudgetType type, @RequestParam Integer budgetId) {
        log.info("getAll");
        List<BudgetItem> items = itemRepository.getAllByTypeOrderByIdDesc(type);
        List<Transaction> transactions  = transactionRepository.getAllByBudgetItemIdBetween(
                2024, // TODO: replace with request param in the future
                items.getLast().getId(),
                items.getFirst().getId()
        );
        HashMap<Integer, BigDecimal[]> transactionMonths = getTransactionMonths(transactions);

        List<BudgetMonth> plans = planRepository.getAllByBudgetIdOrderByBudgetItemId(budgetId);
        HashMap<Integer, BudgetDataDto> planDtos = getPlanDtos(plans, transactionMonths);

        List<BudgetItemDto> itemDtos = getItemDtos(items, planDtos);
        return budgetDto(itemDtos, type);
    }

    private HashMap<Integer, BigDecimal[]> getTransactionMonths(List<Transaction> transactions) {
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

    private static HashMap<Integer, BudgetDataDto> getPlanDtos(List<BudgetMonth> plans, HashMap<Integer, BigDecimal[]> transactionMonths) {
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
                        budgetMonth.getSum()
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

    private List<BudgetItemDto> getItemDtos(List<BudgetItem> items, HashMap<Integer, BudgetDataDto> planTotals) {
        Map<Integer, BudgetItemDto> itemMap = new HashMap<>();
        List<BudgetItemDto> rootItems = new ArrayList<>();

        for (BudgetItem item : items) {
            BudgetDataDto data = planTotals.get(item.id());
            itemMap.put(item.getId(), new BudgetItemDto(data));
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

    private BudgetDto budgetDto(List<BudgetItemDto> items, BudgetType budgetType) {
        BudgetDataDto totals = new BudgetDataDto();
        totals.setName("Итого " + budgetType.getText().toLowerCase());
        totals.setActualTotal(BigDecimal.valueOf(0));
        totals.setPlanTotal(BigDecimal.valueOf(0));
        BudgetDataMonthDto[] totalMonths = new BudgetDataMonthDto[12];

        for (BudgetItemDto item : items) {
            BudgetDataDto data = item.getData();
            totals.setActualTotal(totals.getActualTotal().add(data.getActualTotal()));
            totals.setPlanTotal(totals.getPlanTotal().add(data.getPlanTotal()));

            BudgetDataMonthDto[] months = data.getMonths();

            for (int i = 0; i < months.length; i++) {
                BudgetDataMonthDto month = new BudgetDataMonthDto();
                month.setIndex(months[i].getIndex());
                month.setActual(months[i].getActual().add(month.getActual() != null ? month.getActual() : BigDecimal.valueOf(0)));
                month.setPlan(months[i].getPlan().add(month.getPlan() != null ? month.getPlan() : BigDecimal.valueOf(0)));

                if (totalMonths[i] == null) {
                    totalMonths[i] = month;
                } else {
                    totalMonths[i].setActual(totalMonths[i].getActual().add(month.getActual()));
                    totalMonths[i].setPlan(totalMonths[i].getPlan().add(month.getPlan()));
                }
            }
        }

        totals.setMonths(totalMonths);

        Collections.sort(items);

        return new BudgetDto(items, totals);
    }

}
