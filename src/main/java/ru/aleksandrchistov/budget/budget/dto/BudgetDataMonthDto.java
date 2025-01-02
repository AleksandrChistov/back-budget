package ru.aleksandrchistov.budget.budget.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BudgetDataMonthDto {

    private int id;
    private Byte index;
    private BigDecimal actual;
    private BigDecimal plan;

    public BudgetDataMonthDto(int id, Byte index, BigDecimal actual, BigDecimal plan) {
        setId(id);
        setIndex(index);
        setActual(actual);
        setPlan(plan);
    }

    @Override
    public String toString() {
        return "BudgetItemDto{" +
                "id=" + id +
                ", index=" + index +
                ", actual=" + actual +
                ", plan=" + plan +
                '}';
    }
}