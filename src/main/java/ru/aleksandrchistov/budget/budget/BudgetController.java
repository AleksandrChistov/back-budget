package ru.aleksandrchistov.budget.budget;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.aleksandrchistov.budget.budget.dto.BudgetDataDto;
import ru.aleksandrchistov.budget.budget.dto.BudgetDto;
import ru.aleksandrchistov.budget.budget.dto.BudgetItemDto;
import ru.aleksandrchistov.budget.budget_item.BudgetItem;
import ru.aleksandrchistov.budget.budget_item.BudgetItemRepository;
import ru.aleksandrchistov.budget.shared.model.BudgetType;
import ru.aleksandrchistov.budget.transaction.Transaction;
import ru.aleksandrchistov.budget.transaction.TransactionRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.aleksandrchistov.budget.budget.BudgetUtility.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
@RequestMapping(value = BudgetController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class BudgetController {

    static final String REST_URL = "/api/budgets";
    protected final Logger log = getLogger(getClass());

    @Autowired
    private BudgetRepository repository;

    @Autowired
    private BudgetItemRepository itemRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BudgetPlanRepository planRepository;

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



}
