package ru.aleksandrchistov.budget.pages.report.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TotalMonthDto {

    private String title;
    private BigDecimal plan;
    private BigDecimal actual;

    public TotalMonthDto(String title, BigDecimal plan, BigDecimal actual) {
        setTitle(title);
        setPlan(plan);
        setActual(actual);
    }

    @Override
    public String toString() {
        return "TotalMonthDto{" +
                ", title=" + title +
                ", plan=" + plan +
                ", actual=" + actual +
                '}';
    }
}