package ru.aleksandrchistov.budget.pages.budget.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import ru.aleksandrchistov.budget.pages.transaction.TransactionType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BudgetItemDto implements Comparable<BudgetItemDto>{

    private BudgetDataDto data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<BudgetItemDto> children;

    private TransactionType type;

    public BudgetItemDto(BudgetDataDto data, TransactionType type) {
        setData(data);
        setType(type);
    }

    public void addChild(BudgetItemDto child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.addFirst(child);
    }

    @Override
    public String toString() {
        return "BudgetItemDto{" +
                "data=" + data +
                ", children=" + children +
                ", type=" + type +
                '}';
    }

    @Override
    public int compareTo(BudgetItemDto o) {
        return data.getId().compareTo(o.getData().getId());
    }
}