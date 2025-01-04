package ru.aleksandrchistov.budget.pages.access;

import lombok.Getter;

@Getter
public enum Role {
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
}
