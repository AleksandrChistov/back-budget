package ru.aleksandrchistov.budget.counterparty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aleksandrchistov.budget.common.model.BaseEntity;

@Entity
@Table(name = "counterparty")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Counterparty extends BaseEntity {

    @Column(name = "name", nullable = false)
    @Size(max = 255)
    @NotBlank
    private String name;

    public Counterparty(Counterparty cp) {
        this(cp.id, cp.name);
    }

    public Counterparty(Integer id, String name) {
        super(id);
        setName(name);
    }

    @Override
    public String toString() {
        return "Counterparty{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}