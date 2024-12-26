package ru.aleksandrchistov.budget.account;

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

    @Column(name = "department_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @Min(1)
    private Integer departmentId;

    public Account(Account acc) {
        this(acc.id, acc.num, acc.type, acc.balance, acc.name, acc.departmentId);
    }

    public Account(Integer id, Integer num, AccountType type, BigDecimal balance, String name, Integer departmentId) {
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
                ", type=" + type +
                ", balance=" + balance +
                ", departmentId=" + departmentId +
                ", name='" + name + '\'' +
                '}';
    }

}