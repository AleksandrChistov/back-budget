package ru.aleksandrchistov.budget.budget;


import ru.aleksandrchistov.budget.shared.model.BudgetType;

public enum DefaultBudgetId {
    EXPENSES(1),
    REVENUE(2),
    CAPEX(3),
    CAPITAL(4);

    private final int id;

    DefaultBudgetId(int id) {
        this.id = id;
    }

    public static int fromConst(BudgetType type) {
        for (DefaultBudgetId budget : DefaultBudgetId.values()) {
            if (budget.name().equalsIgnoreCase(type.name())) {
                return budget.id;
            }
        }
        throw new IllegalArgumentException("No id for " + type.name() + " found");
    }
}
