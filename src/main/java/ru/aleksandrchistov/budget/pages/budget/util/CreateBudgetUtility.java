package ru.aleksandrchistov.budget.pages.budget.util;

import lombok.experimental.UtilityClass;
import ru.aleksandrchistov.budget.common.error.NotFoundException;
import ru.aleksandrchistov.budget.pages.budget.dto.BudgetDataDto;
import ru.aleksandrchistov.budget.pages.budget.dto.BudgetDataMonthDto;
import ru.aleksandrchistov.budget.pages.budget.dto.BudgetDto;
import ru.aleksandrchistov.budget.pages.budget.dto.BudgetItemDto;
import ru.aleksandrchistov.budget.pages.budget.model.BudgetMonth;
import ru.aleksandrchistov.budget.pages.budget_item.BudgetItem;
import ru.aleksandrchistov.budget.pages.budget_item.BudgetItemRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class CreateBudgetUtility {

    public static List<BudgetMonth> getNewPlansFromDto(BudgetDto dto, int budgetId, BudgetItemRepository itemRepository) {
        List<BudgetItemDto> items = dto.getBudgetItems();
        List<BudgetMonth> plans = new ArrayList<>();

        return getNewBudgetMonths(items, plans, budgetId, itemRepository);
    }

    private static List<BudgetMonth> getNewBudgetMonths(
            List<BudgetItemDto> items, List<BudgetMonth> plans,
            int budgetId, BudgetItemRepository itemRepository
    ) {
        for (BudgetItemDto item : items) {
            BudgetDataDto data = item.getData();
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

            List<BudgetItemDto> childrenItems = item.getChildren();

            if (childrenItems != null && !childrenItems.isEmpty()) {
                getNewBudgetMonths(childrenItems, plans, budgetId, itemRepository);
            }
        }

        return plans;
    }

    public static List<BudgetMonth> getUpdatedBudgetMonths(List<BudgetItemDto> items, List<BudgetMonth> plans) {
        for (BudgetItemDto item : items) {
            BudgetDataDto data = item.getData();
            BudgetDataMonthDto[] dataMonths = data.getMonths();

            Arrays.stream(dataMonths).forEach(month -> {
                BudgetMonth found = findPlanById(plans, month.getId());
                found.setSum(found.getSum().add(month.getPlan()));
            });

            BudgetMonth found = findPlanById(plans, data.getId());
            found.setSum(found.getSum().add(data.getPlanTotal()));

            List<BudgetItemDto> childrenItems = item.getChildren();

            if (childrenItems != null && !childrenItems.isEmpty()) {
                getUpdatedBudgetMonths(childrenItems, plans);
            }
        }

        return plans;
    }

    private BudgetMonth findPlanById(List<BudgetMonth> plans, int id) {
        BudgetMonth found = plans.stream()
                .filter(plan -> plan.getId().equals(id))
                .findAny()
                .orElse(null);
        if (found == null) {
            throw new NotFoundException("Budget plan with ID " + id + " not found");
        }
        return found;
    }

}
