package ru.aleksandrchistov.budget.pages.budget_item;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.aleksandrchistov.budget.shared.model.BudgetType;
import ru.aleksandrchistov.budget.pages.transaction.TransactionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

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
        return budgetItemDtoList(items);
    }

    private List<BudgetItemDto> budgetItemDtoList(List<BudgetItem> items) {
        Map<Integer, BudgetItemDto> itemMap = new HashMap<>();
        List<BudgetItemDto> rootItems = new ArrayList<>();

        for (BudgetItem item : items) {
            itemMap.put(item.getId(), new BudgetItemDto(item.id(), item.getName()));
        }

        for (BudgetItem item : items) {
            if (item.getParentId() == null) {
                BudgetItemDto itemDto = itemMap.get(item.getId());
                rootItems.add(itemDto);
            } else {
                if (itemMap.containsKey(item.getParentId())) {
                    BudgetItemDto parent = itemMap.get(item.getParentId());
                    BudgetItemDto child = itemMap.get(item.getId());
                    parent.addChild(child);
                }
            }
        }

        return rootItems;
    }
}
