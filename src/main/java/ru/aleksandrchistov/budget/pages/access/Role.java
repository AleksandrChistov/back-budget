package ru.aleksandrchistov.budget.pages.access;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum Role implements GrantedAuthority {
    ADMIN("Админ"),
    MANAGER("Менеджер"),
    ANALYST("Аналитик");

    private final String text;

    Role(String text) {
        this.text = text;
    }

    public static Role fromString(String text) {
        for (Role type : Role.values()) {
            if (type.text.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

    @Override
    public String getAuthority() {
        //   https://stackoverflow.com/a/19542316/548473
        return "ROLE_" + name();
    }
}
