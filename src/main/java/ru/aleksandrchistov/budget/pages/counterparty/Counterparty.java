package ru.aleksandrchistov.budget.pages.counterparty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aleksandrchistov.budget.common.model.BaseEntity;

import java.math.BigInteger;

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

    @Column(name = "inn", nullable = false)
    @NotNull
    private BigInteger inn;

    public Counterparty(Counterparty cp) {
        this(cp.id, cp.name, cp.inn);
    }

    public Counterparty(Integer id, String name, BigInteger inn) {
        super(id);
        setName(name);
        setInn(inn);
    }

    @Override
    public String toString() {
        return "Counterparty{" +
                "id=" + id +
                "inn=" + inn +
                ", name='" + name + '\'' +
                '}';
    }

}