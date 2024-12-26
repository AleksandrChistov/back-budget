package ru.aleksandrchistov.budget.budget_item;

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

@Entity
@Table(name = "budget_item")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BudgetItem extends BaseEntity {

    @Column(name = "type", nullable = false, length = 10)
    @Size(max = 10)
    @NotNull
    private String type;

    @Column(name = "name", nullable = false, length = 256)
    @Size(max = 256)
    @NotBlank
    private String name;

    @Column(name = "parent_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @Min(1)
    private Integer parentId;

    public BudgetItem(BudgetItem item) {
        this(item.id, item.type, item.name, item.parentId);
    }

    public BudgetItem(Integer id, String type, String name, Integer parentId) {
        super(id);
        setType(type);
        setName(name);
        setParentId(parentId);
    }

    @Override
    public String toString() {
        return "BudgetItem{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                '}';
    }
}