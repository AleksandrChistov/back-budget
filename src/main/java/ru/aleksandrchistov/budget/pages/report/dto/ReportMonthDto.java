package ru.aleksandrchistov.budget.pages.report.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
@Setter
@NoArgsConstructor
public class ReportMonthDto {

    private String title;
    private BigDecimal[] plan;
    private BigDecimal[] actual;

    public ReportMonthDto(String title, BigDecimal[] plan, BigDecimal[] actual) {
        setTitle(title);
        setPlan(plan);
        setActual(actual);
    }

    @Override
    public String toString() {
        return "ReportMonthDto{" +
                "title=" + title +
                ", plan=" + Arrays.toString(plan) +
                ", actual=" + Arrays.toString(actual) +
                '}';
    }
}