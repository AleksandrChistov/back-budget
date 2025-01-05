package ru.aleksandrchistov.budget.pages.budget.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import ru.aleksandrchistov.budget.pages.budget_item.BudgetItem;
import ru.aleksandrchistov.budget.common.model.BaseEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "budget_plan")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BudgetMonth extends BaseEntity {

    @Range(min = 0, max = 12)
    @Column(name = "index", nullable = false)
    private Byte index;

    @NotNull
    @Column(name = "sum", nullable = false)
    private BigDecimal sum;

    @Min(1)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "budget_id")
    private Integer budgetId;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "budget_item_id", nullable = false)
    private BudgetItem budgetItem;

    public BudgetMonth(BudgetMonth plan) {
        this(plan.id, plan.index, plan.sum, plan.budgetId, plan.budgetItem);
    }

    public BudgetMonth(Integer id, Byte index, BigDecimal sum, Integer budgetId, BudgetItem budgetItem) {
        super(id);
        setIndex(index);
        setSum(sum);
        setBudgetId(budgetId);
        setBudgetItem(budgetItem);
    }

    @Override
    public String toString() {
        return "BudgetMonth{" +
                "id=" + id +
                ", index=" + index +
                ", sum=" + sum +
                ", budgetId=" + budgetId +
                ", budgetItem=" + budgetItem +
                '}';
    }

}