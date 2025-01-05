package ru.aleksandrchistov.budget.pages.budget.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BudgetDto {

    private List<BudgetItemDto> budgetItems;

    private BudgetDataDto totals;

    private Integer departmentId;

    public BudgetDto(List<BudgetItemDto> budgetItems, BudgetDataDto totals) {
        setBudgetItems(budgetItems);
        setTotals(totals);
    }

    @Override
    public String toString() {
        return "BudgetDto{" +
                "budgetItems=" + budgetItems +
                ", totals=" + totals +
                ", departmentId=" + departmentId +
                '}';
    }
}