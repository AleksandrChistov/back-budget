package ru.aleksandrchistov.budget.department;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aleksandrchistov.budget.common.model.BaseEntity;

@Entity
@Table(
        name = "departments",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "label"}, name = "dep_label_unique")}
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department extends BaseEntity {

    @Column(name = "label", nullable = false, length = 256)
    @Size(max = 256)
    @NotBlank
    private String label;

    public Department(Department dep) {
        this(dep.id, dep.label);
    }

    public Department(Integer id, String label) {
        super(id);
        setLabel(label);
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", label='" + label + '\'' +
                '}';
    }
}