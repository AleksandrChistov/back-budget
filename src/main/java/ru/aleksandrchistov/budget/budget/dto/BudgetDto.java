package ru.aleksandrchistov.budget.budget.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BudgetDto {

    private List<BudgetItemDto> budgetItems;

    private BudgetDataDto totals;

    public BudgetDto(List<BudgetItemDto> budgetItems, BudgetDataDto totals) {
        setBudgetItems(budgetItems);
        setTotals(totals);
    }

    @Override
    public String toString() {
        return "BudgetItemDto{" +
                "budgetItems=" + budgetItems +
                ", totals=" + totals +
                '}';
    }
}