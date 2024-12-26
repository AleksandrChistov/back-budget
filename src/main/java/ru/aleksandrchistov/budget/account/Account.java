package ru.aleksandrchistov.budget.account;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aleksandrchistov.budget.common.model.BaseEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {
    @Column(name = "num", unique = true, nullable = false)
    @NotNull
    private Integer num;

    @Column(name = "type", nullable = false, length = 5)
    @Enumerated(EnumType.STRING)
    @NotNull
    private AccountType type;

    @Column(name = "balance", nullable = false)
    @NotNull
    private BigDecimal balance;

    @Column(name = "name", nullable = false)
    @NotBlank
    @Size(max = 256)
    private String name;

}