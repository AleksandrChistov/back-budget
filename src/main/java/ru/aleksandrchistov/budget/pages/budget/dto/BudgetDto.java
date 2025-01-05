package ru.aleksandrchistov.budget.pages.budget.dto;

import lombok.Getter;
import lombok.Setter;
import ru.aleksandrchistov.budget.common.HasId;

import java.util.List;

@Getter
@Setter
public class BudgetDto implements HasId {

    private Integer id;

    private List<BudgetItemDto> budgetItems;

    private BudgetDataDto totals;

    private Integer departmentId;

    public BudgetDto(Integer id, List<BudgetItemDto> budgetItems, BudgetDataDto totals) {
        setId(id);
        setBudgetItems(budgetItems);
        setTotals(totals);
    }

    @Override
    public String toString() {
        return "BudgetDto{" +
                "id=" + id +
                ", budgetItems=" + budgetItems +
                ", totals=" + totals +
                ", departmentId=" + departmentId +
                '}';
    }
}