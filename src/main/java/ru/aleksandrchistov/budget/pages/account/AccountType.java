package ru.aleksandrchistov.budget.pages.account;

import lombok.Getter;

@Getter
public enum AccountType {
    BANK("Банковский"),
    CASH("Наличные");

    private final String text;

    AccountType(String text) {
        this.text = text;
    }

    public static AccountType fromString(String text) {
        for (AccountType type : AccountType.values()) {
            if (type.text.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
