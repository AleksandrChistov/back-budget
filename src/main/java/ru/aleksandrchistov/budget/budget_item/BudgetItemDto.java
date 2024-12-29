package ru.aleksandrchistov.budget.budget_item;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BudgetItemDto {

    private int id;
    private String label;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<BudgetItemDto> children;

    public BudgetItemDto(int id, String name) {
        setId(id);
        setLabel(name);
    }

    public void addChild(BudgetItemDto child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

    @Override
    public String toString() {
        return "BudgetItemDto{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", children=" + children +
                '}';
    }
}