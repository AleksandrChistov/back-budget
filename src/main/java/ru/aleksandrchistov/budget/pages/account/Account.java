package ru.aleksandrchistov.budget.pages.account;

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

import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {

    @NotNull
    @Column(name = "num", unique = true, nullable = false)
    private BigInteger num;

    @NotNull
    @Size(max = 5)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 5)
    private AccountType type;

    @NotNull
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @NotBlank
    @Size(max = 256)
    @Column(name = "name", nullable = false)
    private String name;

    @Min(1)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "department_id")
    private Integer departmentId;

    public Account(Account acc) {
        this(acc.id, acc.num, acc.type, acc.balance, acc.name, acc.departmentId);
    }

    public Account(Integer id, BigInteger num, AccountType type, BigDecimal balance, String name, Integer departmentId) {
        super(id);
        setNum(num);
        setType(type);
        setBalance(balance);
        setName(name);
        setDepartmentId(departmentId);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", num=" + num +
                ", type=" + type.getText() +
                ", balance=" + balance +
                ", departmentId=" + departmentId +
                ", name='" + name + '\'' +
                '}';
    }

}