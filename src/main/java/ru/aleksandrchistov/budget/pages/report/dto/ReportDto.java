package ru.aleksandrchistov.budget.pages.report.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class ReportDto {

    private TotalMonthDto[] totals;

    private ReportMonthDto[] reports;

    public ReportDto(TotalMonthDto[] totals, ReportMonthDto[] reports) {
        setTotals(totals);
        setReports(reports);
    }

    @Override
    public String toString() {
        return "ReportDto{" +
                "totals=" + Arrays.toString(totals) +
                ", reports=" + Arrays.toString(reports) +
                '}';
    }
}
