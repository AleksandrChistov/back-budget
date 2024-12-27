package ru.aleksandrchistov.budget.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.aleksandrchistov.budget.common.model.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseEntity {

    @Column(name = "sum", nullable = false)
    @NotNull
    private BigDecimal sum;

    @Column(name = "type", nullable = false, length = 10)
    @Size(max = 10)
    @NotNull
    private TransactionType type;

    @Column(name = "payment_date", nullable = false)
    @NotNull
    private LocalDate paymentDate;

    @Column(name = "description", nullable = false, length = 256)
    @Size(max = 256)
    @NotNull
    private String description;

    @Column(name = "department_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @Min(1)
    private Integer departmentId;

    @Column(name = "account_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Min(1)
    @NotNull
    private Integer accountId;

    @Column(name = "budget_item_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Min(1)
    @NotNull
    private Integer budgetItemId;

    @Column(name = "counterparty_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Min(1)
    @NotNull
    private Integer counterpartyId;

    public Transaction(Transaction tr) {
        this(tr.id, tr.sum, tr.type, tr.paymentDate, tr.description, tr.departmentId, tr.accountId, tr.budgetItemId, tr.counterpartyId);
    }

    public Transaction(Integer id, BigDecimal sum, TransactionType type,
                       LocalDate paymentDate, String description,
                       Integer departmentId, Integer accountId, Integer budgetItemId, Integer counterpartyId) {
        super(id);
        setSum(sum);
        setType(type);
        setPaymentDate(paymentDate);
        setDescription(description);
        setDepartmentId(departmentId);
        setAccountId(accountId);
        setBudgetItemId(budgetItemId);
        setCounterpartyId(counterpartyId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sum=" + sum +
                ", type=" + type +
                ", paymentDate=" + paymentDate +
                ", description='" + description + '\'' +
                ", departmentId=" + departmentId +
                ", accountId=" + accountId +
                ", budgetItemId=" + budgetItemId +
                ", counterpartyId=" + counterpartyId +
                '}';
    }
}