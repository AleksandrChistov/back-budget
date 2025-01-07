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
                        Arrays.asList(new Integer[]{28, 29, 30, 31, 32, 34, 38, 39})
                                .contains(transaction.getKey())
        );

        BigDecimal[] salaries = calcExpense(
                months,
                transaction ->
                        Arrays.asList(new Integer[]{34, 35, 36})
                                .contains(transaction.getKey())
        );

        BigDecimal[] taxes = calcExpense(
                months,
                transaction ->
                        Arrays.asList(new Integer[]{26, 35, 36})
                                .contains(transaction.getKey())
        );

        BigDecimal[] percents = calcExpense(
                months,
                transaction ->
                        Arrays.asList(new Integer[]{62, 63, 68, 69})
                                .contains(transaction.getKey())
        );

        return new BigDecimal[][]{operExpenses, salaries, taxes, percents};
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
                transaction ->
                        Arrays.asList(new Integer[]{3, 4, 5, 7, 8, 9, 10, 13, 14, 15, 17, 18, 19, 20})
                                .contains(transaction.getKey()),
                transaction ->
                        Arrays.asList(new Integer[]{28, 29, 30, 31, 32, 34, 35, 36, 38, 39})
                                .contains(transaction.getKey())
        );

        BigDecimal[] ebitda = calcProfit(
                months,
                transaction ->
                        Arrays.asList(new Integer[]{3, 4, 5, 7, 8, 9, 10, 13, 14, 15, 17, 18, 19, 20, 22, 23, 25, 50, 51, 52, 53, 54, 56, 57, 59, 60, 65, 66})
                                .contains(transaction.getKey()),
                transaction ->
                        Arrays.asList(new Integer[]{28, 29, 30, 31, 34, 35, 36, 38, 39, 41, 42, 43, 44, 45, 47, 48})
                                .contains(transaction.getKey())
        );

        BigDecimal[] finProfit = calcProfit(
                months,
                transaction ->
                        Arrays.asList(new Integer[]{3, 4, 5, 7, 8, 9, 10, 13, 14, 15, 17, 18, 19, 20, 22, 23, 25, 50, 51, 52, 53, 54, 56, 57, 59, 60, 65, 66})
                                .contains(transaction.getKey()),
                transaction ->
                        Arrays.asList(new Integer[]{28, 29, 30, 31, 32, 34, 35, 36, 38, 39, 41, 42, 43, 44, 45, 47, 48, 62, 63, 68, 69})
                                .contains(transaction.getKey())
        );

        BigDecimal[] netProfit = calcProfit(
                months,
                transaction ->
                        Arrays.asList(new Integer[]{3, 4, 5, 7, 8, 9, 10, 13, 14, 15, 17, 18, 19, 20, 22, 23, 25, 50, 51, 52, 53, 54, 56, 57, 59, 60, 65, 66})
                                .contains(transaction.getKey()),
                transaction ->
                        Arrays.asList(new Integer[]{26, 28, 29, 30, 31, 32, 34, 35, 36, 38, 39, 41, 42, 43, 44, 45, 47, 48, 62, 63, 68, 69})
                                .contains(transaction.getKey())
        );

        return new BigDecimal[][]{operProfit, ebitda, finProfit, netProfit};
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
