package ru.aleksandrchistov.budget.pages.transaction;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TransactionDto {

    @NotNull
    private BigDecimal sum;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @NotNull
    private LocalDate paymentDate;

    @NotNull
    @Size(max = 256)
    private String description;

    @Min(1)
    private Integer accountId;

    @Min(1)
    private Integer budgetItemId;

    @Min(1)
    private Integer counterpartyId;

    @Min(1)
    private Integer departmentId;

    public TransactionDto(BigDecimal sum, TransactionType type,
                          LocalDate paymentDate, String description,
                          Integer accountId, Integer budgetItemId,
                          Integer counterpartyId, Integer departmentId) {
        setSum(sum);
        setType(type);
        setPaymentDate(paymentDate);
        setDescription(description);
        setAccountId(accountId);
        setBudgetItemId(budgetItemId);
        setCounterpartyId(counterpartyId);
        setDepartmentId(departmentId);
    }

    @Override
    public String toString() {
        return "TransactionDto{" +
                "sum=" + sum +
                ", type=" + type +
                ", paymentDate=" + paymentDate +
                ", description='" + description + '\'' +
                ", accountId=" + accountId +
                ", budgetItemId=" + budgetItemId +
                ", counterpartyId=" + counterpartyId +
                ", departmentId=" + departmentId +
                '}';
    }
}