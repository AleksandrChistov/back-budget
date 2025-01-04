package ru.aleksandrchistov.budget.pages.budget.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class BudgetDataDto {

    private Integer id;
    private String name;
    private BigDecimal actualTotal;
    private BigDecimal planTotal;
    private BudgetDataMonthDto[] months = new BudgetDataMonthDto[12];

    public BudgetDataDto(Integer id, String name, BigDecimal actualTotal, BigDecimal planTotal, BudgetDataMonthDto[] months) {
        setId(id);
        setName(name);
        setActualTotal(actualTotal);
        setPlanTotal(planTotal);
        setMonths(months);
    }

    @Override
    public String toString() {
        return "BudgetItemDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", actualTotal=" + actualTotal +
                ", planTotal=" + planTotal +
                ", months=" + Arrays.toString(months) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BudgetDataDto dataDto = (BudgetDataDto) o;
        return Objects.equals(id, dataDto.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id;
    }

}