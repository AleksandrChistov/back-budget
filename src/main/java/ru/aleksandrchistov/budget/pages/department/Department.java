package ru.aleksandrchistov.budget.pages.department;

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
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "name"}, name = "dep_name_unique")}
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department extends BaseEntity {

    @Column(name = "name", nullable = false, length = 256)
    @Size(max = 256)
    @NotBlank
    private String name;

    public Department(Department dep) {
        this(dep.id, dep.name);
    }

    public Department(Integer id, String name) {
        super(id);
        setName(name);
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}