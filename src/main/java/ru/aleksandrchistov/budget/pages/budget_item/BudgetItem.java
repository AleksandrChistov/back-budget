package ru.aleksandrchistov.budget.pages.budget_item;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.aleksandrchistov.budget.common.model.BaseEntity;
import ru.aleksandrchistov.budget.shared.model.BudgetType;
import ru.aleksandrchistov.budget.pages.transaction.TransactionType;

@Entity
@Table(name = "budget_item")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BudgetItem extends BaseEntity {

    @NotNull
    @Size(max = 10)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private BudgetType type;

    @NotNull
    @Size(max = 10)
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 10)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private TransactionType transactionType;

    @NotBlank
    @Size(max = 256)
    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Min(1)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "parent_id")
    private Integer parentId;

    public BudgetItem(BudgetItem item) {
        this(item.id, item.type, item.name, item.parentId);
    }

    public BudgetItem(Integer id, BudgetType type, String name, Integer parentId) {
        super(id);
        setType(type);
        setName(name);
        setParentId(parentId);
    }

    @Override
    public String toString() {
        return "BudgetItem{" +
                "id=" + id +
                ", type='" + type.getText() + '\'' +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                '}';
    }
}