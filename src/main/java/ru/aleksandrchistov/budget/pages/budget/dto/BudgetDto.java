package ru.aleksandrchistov.budget.pages.budget.dto;

import lombok.Getter;
import lombok.Setter;
import ru.aleksandrchistov.budget.shared.model.BudgetType;

import java.util.List;

@Getter
@Setter
public class BudgetDto {

    private List<BudgetItemDto> budgetItems;

    private BudgetDataDto totals;

    private BudgetType type;

    private Integer departmentId;

    public BudgetDto(List<BudgetItemDto> budgetItems, BudgetDataDto totals, BudgetType type) {
        setBudgetItems(budgetItems);
        setTotals(totals);
        setType(type);
    }

    @Override
    public String toString() {
        return "BudgetItemDto{" +
                "budgetItems=" + budgetItems +
                ", totals=" + totals +
                ", type=" + type +
                ", departmentId=" + departmentId +
                '}';
    }
}