package ru.aleksandrchistov.budget.access;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aleksandrchistov.budget.common.model.BaseEntity;

@Entity
@Table(
        name = "accesses",
        uniqueConstraints = { @UniqueConstraint(columnNames = {"id", "role"}, name = "user_role_unique") }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @NotBlank
    @Size(min = 2, max = 256)
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Email
    @NotBlank
    @Size(max = 128)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Size(max = 256)
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role;

    public User(User u) {
        this(u.id, u.fullName, u.email, u.password, u.role);
    }

    public User(Integer id, String fullName, String email, String password, Role role) {
        super(id);
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role.getText() +
                '}';
    }

}
