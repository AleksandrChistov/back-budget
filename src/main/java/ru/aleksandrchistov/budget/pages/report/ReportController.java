package ru.aleksandrchistov.budget.pages.report;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.aleksandrchistov.budget.pages.budget.BudgetPlanRepository;
import ru.aleksandrchistov.budget.pages.budget.model.BudgetMonth;
import ru.aleksandrchistov.budget.pages.report.dto.ReportDto;
import ru.aleksandrchistov.budget.pages.report.dto.ReportMonthDto;
import ru.aleksandrchistov.budget.pages.report.dto.TotalMonthDto;
import ru.aleksandrchistov.budget.pages.transaction.Transaction;
import ru.aleksandrchistov.budget.pages.transaction.TransactionRepository;
import ru.aleksandrchistov.budget.pages.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.aleksandrchistov.budget.pages.report.ReportUtility.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
@RequestMapping(value = ReportController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {

    static final String REST_URL = "/api/reports";
    protected final Logger log = getLogger(getClass());

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BudgetPlanRepository planRepository;

    @GetMapping
    public ReportDto getAll(@RequestParam TransactionType type, @Nullable @RequestParam Integer departmentId, @Nullable @RequestParam Integer budgetId) {
        log.info("getAll {}, {}, {}", type, departmentId, budgetId);
        List<Transaction> transactions;
        if (departmentId != null) {
            transactions = transactionRepository.getAllByDepartmentId(departmentId);
        } else {
            transactions = transactionRepository.findAll();
        }

        // Транзакции (actual) по статье бюджета по месяцам
        Map<Integer, BigDecimal[]> transactionMonths = getTransactionMonths(transactions);

        List<BudgetMonth> planMoths = planRepository.getAllByBudgetId(budgetId);
        // Планы (plan) по статье бюджета по месяцам
        Map<Integer, BigDecimal[]> planDtos = getPlanMonths(planMoths);

        if (type == TransactionType.INCOME) {
            String[] titles = new String[]{"Операционная прибыль", "EBITDA", "Прибыль до налогов", "Чистая прибыль"};
            BigDecimal[][] plans = calcProfits(planDtos);
            BigDecimal[][] actuals = calcProfits(transactionMonths);

            TotalMonthDto operProfitTotal = getTotalDto(titles[0], plans[0], actuals[0]);
            TotalMonthDto ebitdaTotal = getTotalDto(titles[1], plans[1], actuals[1]);
            TotalMonthDto finProfitTotal = getTotalDto(titles[2], plans[2], actuals[2]);
            TotalMonthDto netProfitTotal = getTotalDto(titles[3], plans[3], actuals[3]);

            ReportMonthDto operProfitReport = new ReportMonthDto(titles[0], plans[0], actuals[0]);
            ReportMonthDto ebitdaReport = new ReportMonthDto(titles[1], plans[1], actuals[1]);
            ReportMonthDto finProfitReport = new ReportMonthDto(titles[2], plans[2], actuals[2]);
            ReportMonthDto netProfitReport = new ReportMonthDto(titles[3], plans[3], actuals[3]);

            TotalMonthDto[] totals = new TotalMonthDto[]{operProfitTotal, ebitdaTotal, finProfitTotal, netProfitTotal};
            ReportMonthDto[] reports = new ReportMonthDto[]{operProfitReport, ebitdaReport, finProfitReport, netProfitReport};

            return new ReportDto(totals, reports);
        }

        // TODO: implement calculations for expenses reports
        // Все EXPENSE не вычитаем, а складываем?
        String[] titles = new String[]{"Операционные расходы", "Оплата труда", "Налоги", "Проценты к уплате"};
        BigDecimal[][] plans = calcProfits(planDtos);
        BigDecimal[][] actuals = calcProfits(transactionMonths);

        TotalMonthDto operProfitTotal = getTotalDto(titles[0], plans[0], actuals[0]);
        TotalMonthDto ebitdaTotal = getTotalDto(titles[1], plans[1], actuals[1]);
        TotalMonthDto finProfitTotal = getTotalDto(titles[2], plans[2], actuals[2]);
        TotalMonthDto netProfitTotal = getTotalDto(titles[3], plans[3], actuals[3]);

        ReportMonthDto operProfitReport = new ReportMonthDto(titles[0], plans[0], actuals[0]);
        ReportMonthDto ebitdaReport = new ReportMonthDto(titles[1], plans[1], actuals[1]);
        ReportMonthDto finProfitReport = new ReportMonthDto(titles[2], plans[2], actuals[2]);
        ReportMonthDto netProfitReport = new ReportMonthDto(titles[3], plans[3], actuals[3]);

        TotalMonthDto[] totals = new TotalMonthDto[]{operProfitTotal, ebitdaTotal, finProfitTotal, netProfitTotal};
        ReportMonthDto[] reports = new ReportMonthDto[]{operProfitReport, ebitdaReport, finProfitReport, netProfitReport};

        return new ReportDto(totals, reports);

        /*
        * Если type == TransactionTypes.EXPENSE, тогда формируем четыре объекта
        *   Операционные расходы
Операционные расходы — это повседневные затраты компании для ведения бизнеса, производства продуктов и услуг.
заработная плата сотрудников; аренда офисов; расходы на ремонт или техническое обслуживание имущества;
охрана и благоустройство территории; коммунальные услуги; обучение сотрудников; лицензии на ПО или патенты.
Операционные затраты противопоставляются прямым затратам — расходам компании на непосредственное создание товаров и услуг.
BudgetType.EXPENSE (27-39)
        *   Оплата труда
BudgetType.EXPENSE (34-36)
        *   Налоги (на прибыль, сраховые взносы)
BudgetType.EXPENSES (26 + 35-36)
        *   Проценты к уплате
BudgetType.CAPITAL (61-63 и 67-69)
        *
        * */

//        List<BudgetItemDto> itemDtos = getItemDtos(items, planDtos);
//
//        return budgetDto(itemDtos, type);
    }

    private BigDecimal[][] calcProfits(Map<Integer, BigDecimal[]> months) {
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

    private BigDecimal[] calcProfit(
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
