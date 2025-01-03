package ru.aleksandrchistov.budget.budget;

import lombok.experimental.UtilityClass;
import ru.aleksandrchistov.budget.budget.dto.BudgetDataDto;
import ru.aleksandrchistov.budget.budget.dto.BudgetDataMonthDto;
import ru.aleksandrchistov.budget.budget.dto.BudgetDto;
import ru.aleksandrchistov.budget.budget.dto.BudgetItemDto;
import ru.aleksandrchistov.budget.budget_item.BudgetItem;
import ru.aleksandrchistov.budget.budget_item.BudgetItemRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class CreateBudgetUtility {

    public static List<BudgetMonth> getPlansFromDto(BudgetDto dto, int budgetId, BudgetItemRepository itemRepository) {
        List<BudgetMonth> plans = new ArrayList<>();
        List<BudgetItemDto> items = dto.getBudgetItems();

        return getBudgetMonths(items, plans, budgetId, itemRepository);
    }

    private static List<BudgetMonth> getBudgetMonths(
            List<BudgetItemDto> items, List<BudgetMonth> plans,
            int budgetId, BudgetItemRepository itemRepository
    ) {
        for (int i = 0; i < items.size(); i++) {
            BudgetDataDto data = items.get(i).getData();
            BudgetDataMonthDto[] dataMonths = data.getMonths();
            BudgetItem budgetItem = itemRepository.getReferenceById(dataMonths[0].getBudgetItemId());

            Arrays.stream(dataMonths).forEach(month ->
                    plans.add(new BudgetMonth(
                            null,
                            month.getIndex(),
                            month.getPlan(),
                            budgetId,
                            budgetItem
                    )));

            plans.add(new BudgetMonth(
                    null,
                    (byte) 12,
                    data.getPlanTotal(),
                    budgetId,
                    budgetItem
            ));

            List<BudgetItemDto> childrenItems = items.get(i).getChildren();

            if (childrenItems != null && !childrenItems.isEmpty()) {
                getBudgetMonths(childrenItems, plans, budgetId, itemRepository);
            }
        }

        return plans;
    }

}
