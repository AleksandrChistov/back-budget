package ru.aleksandrchistov.budget.pages.report;

import lombok.experimental.UtilityClass;
import ru.aleksandrchistov.budget.pages.budget.model.BudgetMonth;
import ru.aleksandrchistov.budget.pages.report.dto.ReportDto;
import ru.aleksandrchistov.budget.pages.report.dto.ReportMonthDto;
import ru.aleksandrchistov.budget.pages.report.dto.TotalMonthDto;
import ru.aleksandrchistov.budget.pages.transaction.Transaction;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@UtilityClass
public class ReportUtility {

    public static Map<Integer, BigDecimal[]> getTransactionMonths(List<Transaction> transactions) {
        Map<Integer, BigDecimal[]> transactionMonths = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (transactionMonths.containsKey(transaction.getBudgetItem().getId())) {
                BigDecimal[] trMonths = transactionMonths.get(transaction.getBudgetItem().getId());
                BigDecimal monthSum = trMonths[transaction.getPaymentDate().getMonthValue() - 1];
                trMonths[transaction.getPaymentDate().getMonthValue() - 1] = monthSum != null ? monthSum.add(transaction.getSum()) : transaction.getSum();
            } else {
                BigDecimal[] trMonths = new BigDecimal[12];
                trMonths[transaction.getPaymentDate().getMonthValue() - 1] = transaction.getSum();
                transactionMonths.put(transaction.getBudgetItem().getId(), trMonths);
            }
        }
        return transactionMonths;
    }

    public static Map<Integer, BigDecimal[]> getPlanMonths(List<BudgetMonth> plans) {
        Map<Integer, BigDecimal[]> planMonths = new HashMap<>();
        for (BudgetMonth plan : plans) {
            if (plan.getIndex() < 12) {
                if (planMonths.containsKey(plan.getBudgetItem().getId())) {
                    BigDecimal[] months = planMonths.get(plan.getBudgetItem().getId());
                    BigDecimal monthSum = months[plan.getIndex()];
                    months[plan.getIndex()] = monthSum != null ? monthSum.add(plan.getSum()) : plan.getSum();
                } else {
                    BigDecimal[] months = new BigDecimal[12];
                    months[plan.getIndex()] = plan.getSum();
                    planMonths.put(plan.getBudgetItem().getId(), months);
                }
            }
        }
        return planMonths;
    }

    public static ReportDto getReportDto(String[] titles, BigDecimal[][] plans, BigDecimal[][] actuals) {
        TotalMonthDto total1 = getTotalDto(titles[0], plans[0], actuals[0]);
        TotalMonthDto total2 = getTotalDto(titles[1], plans[1], actuals[1]);
        TotalMonthDto total3 = getTotalDto(titles[2], plans[2], actuals[2]);
        TotalMonthDto total4 = getTotalDto(titles[3], plans[3], actuals[3]);

        ReportMonthDto report1 = new ReportMonthDto(titles[0], plans[0], actuals[0]);
        ReportMonthDto report2 = new ReportMonthDto(titles[1], plans[1], actuals[1]);
        ReportMonthDto report3 = new ReportMonthDto(titles[2], plans[2], actuals[2]);
        ReportMonthDto report4 = new ReportMonthDto(titles[3], plans[3], actuals[3]);

        TotalMonthDto[] totals = new TotalMonthDto[]{total1, total2, total3, total4};
        ReportMonthDto[] reports = new ReportMonthDto[]{report1, report2, report3, report4};

        return new ReportDto(totals, reports);
    }

    private static TotalMonthDto getTotalDto(String title, BigDecimal[] plans, BigDecimal[] actuals) {
        BigDecimal plan = Arrays.stream(plans).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal actual = Arrays.stream(actuals).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new TotalMonthDto(title, plan, actual);
    }

    public static BigDecimal[][] calcExpenses(Map<Integer, BigDecimal[]> months) {
        BigDecimal[] operExpenses = calcExpense(
                months,
                transaction ->
                        (transaction.getKey() > 26 && transaction.getKey() < 35) ||
                        (transaction.getKey() > 36 && transaction.getKey() < 40)
        );

        BigDecimal[] salaries = calcExpense(
                months,
                transaction ->
                        (transaction.getKey() > 33 && transaction.getKey() < 37)
        );

        BigDecimal[] taxes = calcExpense(
                months,
                transaction ->
                        (transaction.getKey() == 26) || (transaction.getKey() > 34 && transaction.getKey() < 37)
        );

        BigDecimal[] percents = calcExpense(
                months,
                transaction ->
                        (transaction.getKey() > 60 && transaction.getKey() < 64) ||
                                (transaction.getKey() > 66 && transaction.getKey() < 70)
        );

        return new BigDecimal[][] {operExpenses, salaries, taxes, percents};
    }

    private static BigDecimal[] calcExpense(Map<Integer, BigDecimal[]> items, Predicate<Map.Entry<Integer, BigDecimal[]>> filterExpenses) {
        BigDecimal[] expenses = new BigDecimal[12];
        Arrays.fill(expenses, BigDecimal.ZERO);

        items.entrySet().stream().filter(filterExpenses)
                .forEach(transaction -> {
                    for (int i = 0; i < expenses.length; i++) {
                        BigDecimal expense = transaction.getValue()[i] != null ? transaction.getValue()[i] : BigDecimal.ZERO;
                        expenses[i] = expenses[i].add(expense);
                    }
                });

        return expenses;
    }

    public static BigDecimal[][] calcProfits(Map<Integer, BigDecimal[]> months) {
        BigDecimal[] operProfit = calcProfit(
                months,
                transaction -> (transaction.getKey() > 0 && transaction.getKey() < 21),
                transaction -> (transaction.getKey() > 26 && transaction.getKey() < 40)
        );

        BigDecimal[] ebitda = calcProfit(
                months,
                transaction ->
                        (transaction.getKey() > 0 && transaction.getKey() < 26) ||
                                (transaction.getKey() > 48 && transaction.getKey() < 61) ||
                                (transaction.getKey() > 63 && transaction.getKey() < 67),
                transaction ->
                        (transaction.getKey() > 26 && transaction.getKey() < 32) ||
                                (transaction.getKey() > 32 && transaction.getKey() < 40) ||
                                (transaction.getKey() > 39 && transaction.getKey() < 49) ||
                                (transaction.getKey() > 66 && transaction.getKey() < 70)
        );

        BigDecimal[] finProfit = calcProfit(
                months,
                transaction ->
                        (transaction.getKey() > 0 && transaction.getKey() < 26) ||
                                (transaction.getKey() > 48 && transaction.getKey() < 61) ||
                                (transaction.getKey() > 63 && transaction.getKey() < 67),
                transaction ->
                        (transaction.getKey() > 26 && transaction.getKey() < 40) ||
                                (transaction.getKey() > 39 && transaction.getKey() < 49) ||
                                (transaction.getKey() > 60 && transaction.getKey() < 64) ||
                                (transaction.getKey() > 66 && transaction.getKey() < 70)
        );

        BigDecimal[] netProfit = calcProfit(
                months,
                transaction ->
                        (transaction.getKey() > 0 && transaction.getKey() < 26) ||
                                (transaction.getKey() > 48 && transaction.getKey() < 61) ||
                                (transaction.getKey() > 63 && transaction.getKey() < 67),
                transaction ->
                        (transaction.getKey() > 25 && transaction.getKey() < 40) ||
                                (transaction.getKey() > 39 && transaction.getKey() < 49) ||
                                (transaction.getKey() > 60 && transaction.getKey() < 64) ||
                                (transaction.getKey() > 66 && transaction.getKey() < 70)
        );

        return new BigDecimal[][] {operProfit, ebitda, finProfit, netProfit};
    }

    private static BigDecimal[] calcProfit(
            Map<Integer, BigDecimal[]> items,
            Predicate<Map.Entry<Integer, BigDecimal[]>> filterProfit,
            Predicate<Map.Entry<Integer, BigDecimal[]>> filterExpenses
    ) {
        BigDecimal[] profits = new BigDecimal[12];
        Arrays.fill(profits, BigDecimal.ZERO);

        items.entrySet().stream().filter(filterProfit)
                .forEach(transaction -> {
                    for (int i = 0; i < profits.length; i++) {
                        BigDecimal profit = transaction.getValue()[i] != null ? transaction.getValue()[i] : BigDecimal.ZERO;
                        profits[i] = profits[i].add(profit);
                    }
                });

        items.entrySet().stream().filter(filterExpenses)
                .forEach(transaction -> {
                    for (int i = 0; i < profits.length; i++) {
                        BigDecimal expense = transaction.getValue()[i] != null ? transaction.getValue()[i] : BigDecimal.ZERO;
                        profits[i] = profits[i].subtract(expense);
                    }
                });

        return profits;
    }

}
