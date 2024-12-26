package ru.aleksandrchistov.budget.shared.model;

import lombok.Getter;

@Getter
public enum BudgetType {
    REVENUE("Доходы"),
    EXPENSES("Расходы"),
    CAPEX("Инвестиции"),
    CAPITAL("Капитал");

    private final String text;

    BudgetType(String text) {
        this.text = text;
    }

    public static BudgetType fromString(String text) {
        for (BudgetType type : BudgetType.values()) {
            if (type.text.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
