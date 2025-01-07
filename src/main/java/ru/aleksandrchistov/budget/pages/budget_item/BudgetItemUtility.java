package ru.aleksandrchistov.budget.pages.budget_item;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class BudgetItemUtility {

    public static List<BudgetItemDto> buildDtoList(List<BudgetItem> items) {
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
