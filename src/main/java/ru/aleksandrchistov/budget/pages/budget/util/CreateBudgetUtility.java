package ru.aleksandrchistov.budget.pages.budget.util;

import lombok.experimental.UtilityClass;
import ru.aleksandrchistov.budget.common.error.NotFoundException;
import ru.aleksandrchistov.budget.pages.budget.dto.BudgetDataDto;
import ru.aleksandrchistov.budget.pages.budget.dto.BudgetDataMonthDto;
import ru.aleksandrchistov.budget.pages.budget.dto.BudgetItemDto;
import ru.aleksandrchistov.budget.pages.budget.model.BudgetMonth;
import ru.aleksandrchistov.budget.pages.budget_item.BudgetItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class CreateBudgetUtility {

    public static List<BudgetMonth> getNewPlansFromItems(int budgetId, List<BudgetItem> items) {
        int BUDGETS_ITEMS_MAX_INDEX = 13;
        List<BudgetMonth> plans = new ArrayList<>();

        for (BudgetItem item : items) {
            for (byte i = 0; i < BUDGETS_ITEMS_MAX_INDEX; i++) {
                BudgetMonth plan = new BudgetMonth(null, i, BigDecimal.ZERO, budgetId, item);
                plans.add(plan);
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
                found.setSum(month.getPlan());
            });

            BudgetMonth found = findPlanById(plans, data.getId());
            found.setSum(data.getPlanTotal());

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
