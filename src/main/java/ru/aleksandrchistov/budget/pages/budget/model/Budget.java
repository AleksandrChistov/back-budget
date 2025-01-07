package ru.aleksandrchistov.budget.pages.budget.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.aleksandrchistov.budget.common.model.BaseEntity;

@Entity
@Table(name = "budget")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Budget extends BaseEntity {

    @NotBlank
    @Size(max = 256)
    @Column(name = "name", nullable = false)
    private String name;

    @Min(1)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "department_id")
    private Integer departmentId;

    @Min(2024)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ColumnDefault("EXTRACT(year FROM CURRENT_DATE)")
    @Column(name = "year")
    private int year;

    public Budget(Budget acc) {
        this(acc.id, acc.name, acc.departmentId);
    }

    public Budget(Integer id, String name, Integer departmentId) {
        super(id);
        setName(name);
        setDepartmentId(departmentId);
    }

    public Budget(Integer id, String name, Integer departmentId, int year) {
        super(id);
        setName(name);
        setDepartmentId(departmentId);
        setYear(year);
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", departmentId=" + departmentId +
                '}';
    }

}