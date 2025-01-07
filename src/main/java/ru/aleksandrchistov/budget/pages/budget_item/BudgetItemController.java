package ru.aleksandrchistov.budget.pages.budget_item;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.aleksandrchistov.budget.pages.transaction.TransactionType;
import ru.aleksandrchistov.budget.shared.model.BudgetType;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.aleksandrchistov.budget.pages.budget_item.BudgetItemUtility.buildDtoList;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
@RequestMapping(value = BudgetItemController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class BudgetItemController {

    static final String REST_URL = "/api/budget-items";
    protected final Logger log = getLogger(getClass());

    @Autowired
    private BudgetItemRepository repository;

    @GetMapping
    public List<BudgetItemDto> getAll(
            @Nullable @RequestParam BudgetType budgetType,
            @Nullable @RequestParam TransactionType transactionType
    ) {
        log.info("getAll");
        List<BudgetItem> items;
        if (budgetType != null && transactionType != null) {
            items = repository.getAllByTypeAndTransactionType(budgetType, transactionType);
        } else if (budgetType != null) {
            items = repository.getAllByType(budgetType);
        } else {
            items = repository.findAll();
        }
        return buildDtoList(items);
    }

}
