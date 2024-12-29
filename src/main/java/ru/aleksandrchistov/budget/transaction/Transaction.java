package ru.aleksandrchistov.budget.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.aleksandrchistov.budget.account.Account;
import ru.aleksandrchistov.budget.budget_item.BudgetItem;
import ru.aleksandrchistov.budget.common.model.BaseEntity;
import ru.aleksandrchistov.budget.counterparty.Counterparty;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseEntity {

    @NotNull
    @Column(name = "sum", nullable = false)
    private BigDecimal sum;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private TransactionType type;

    @NotNull
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @NotNull
    @Size(max = 256)
    @Column(name = "description", nullable = false, length = 256)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "budget_item_id", nullable = false)
    private BudgetItem budgetItem;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "counterparty_id", nullable = false)
    private Counterparty counterparty;

    @Min(1)
    @Column(name = "department_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    private Integer departmentId;

    public Transaction(Transaction tr) {
        this(tr.id, tr.sum, tr.type, tr.paymentDate, tr.description, tr.account, tr.budgetItem, tr.counterparty, tr.departmentId);
    }

    public Transaction(Integer id, BigDecimal sum, TransactionType type,
                       LocalDate paymentDate, String description,
                       Account account, BudgetItem budgetItem,
                       Counterparty counterparty, Integer departmentId) {
        super(id);
        setSum(sum);
        setType(type);
        setPaymentDate(paymentDate);
        setDescription(description);
        setAccount(account);
        setBudgetItem(budgetItem);
        setCounterparty(counterparty);
        setDepartmentId(departmentId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sum=" + sum +
                ", type=" + type +
                ", paymentDate=" + paymentDate +
                ", description='" + description + '\'' +
                ", account=" + account +
                ", budgetItem=" + budgetItem +
                ", counterparty=" + counterparty +
                ", departmentId=" + departmentId +
                '}';
    }
}