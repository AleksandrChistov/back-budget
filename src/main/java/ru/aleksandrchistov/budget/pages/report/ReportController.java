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
        log.info("getAll");
        List<Transaction> transactions;
        if (departmentId != null) {
            transactions = transactionRepository.getAllByDepartmentId(departmentId);
        } else {
            transactions = transactionRepository.findAll();
        }

        // Транзакции (actual) по статье бюджета по месяцам
        Map<Integer, BigDecimal[]> transactionMonths = getTransactionMonths(transactions);

        BigDecimal[] operProfitActual = calcOperProfit(transactionMonths);
        BigDecimal[] ebitdaActual = calcEbitda(transactionMonths);
        BigDecimal[] finProfitActual = calcFinProfit(transactionMonths);
        BigDecimal[] netProfitActual = calcNetProfit(transactionMonths);

        List<BudgetMonth> plans = planRepository.getAllByBudgetId(budgetId);
        // Планы (plan) по статье бюджета по месяцам
        Map<Integer, BigDecimal[]> planDtos = getPlanMonths(plans);

        BigDecimal[] operProfitPlan = calcOperProfit(planDtos);
        BigDecimal[] ebitdaPlan = calcEbitda(planDtos);
        BigDecimal[] finProfitPlan = calcFinProfit(planDtos);
        BigDecimal[] netProfitPlan = calcNetProfit(planDtos);

        if (type == TransactionType.INCOME) {
            TotalMonthDto operProfitTotal = getTotalDto("Операционная прибыль", operProfitPlan, operProfitActual);
            TotalMonthDto ebitdaTotal = getTotalDto("EBITDA", ebitdaPlan, ebitdaActual);
            TotalMonthDto finProfitTotal = getTotalDto("Прибыль до налогов", finProfitPlan, finProfitActual);
            TotalMonthDto netProfitTotal = getTotalDto("Чистая прибыль", netProfitPlan, netProfitActual);

            ReportMonthDto operProfitReport = new ReportMonthDto("Операционная прибыль", operProfitPlan, operProfitActual);
            ReportMonthDto ebitdaReport = new ReportMonthDto("EBITDA", ebitdaPlan, ebitdaActual);
            ReportMonthDto finProfitReport = new ReportMonthDto("Прибыль до налогов", finProfitPlan, finProfitActual);
            ReportMonthDto netProfitReport = new ReportMonthDto("Чистая прибыль", netProfitPlan, netProfitActual);

            TotalMonthDto[] totals = new TotalMonthDto[]{operProfitTotal, ebitdaTotal, finProfitTotal, netProfitTotal};
            ReportMonthDto[] reports = new ReportMonthDto[]{operProfitReport, ebitdaReport, finProfitReport, netProfitReport};

            return new ReportDto(totals, reports);
        } else {
            // TODO: implement calculations for expenses reports
            // Все EXPENSE не вычитаем, а складываем?
            TotalMonthDto operProfitTotal = getTotalDto("Операционные расходы", operProfitPlan, operProfitActual);
            TotalMonthDto ebitdaTotal = getTotalDto("Оплата труда", ebitdaPlan, ebitdaActual);
            TotalMonthDto finProfitTotal = getTotalDto("Налоги", finProfitPlan, finProfitActual);
            TotalMonthDto netProfitTotal = getTotalDto("Проценты к уплате", netProfitPlan, netProfitActual);

            ReportMonthDto operProfitReport = new ReportMonthDto("Операционные расходы", operProfitPlan, operProfitActual);
            ReportMonthDto ebitdaReport = new ReportMonthDto("Оплата труда", ebitdaPlan, ebitdaActual);
            ReportMonthDto finProfitReport = new ReportMonthDto("Налоги", finProfitPlan, finProfitActual);
            ReportMonthDto netProfitReport = new ReportMonthDto("Проценты к уплате", netProfitPlan, netProfitActual);

            TotalMonthDto[] totals = new TotalMonthDto[]{operProfitTotal, ebitdaTotal, finProfitTotal, netProfitTotal};
            ReportMonthDto[] reports = new ReportMonthDto[]{operProfitReport, ebitdaReport, finProfitReport, netProfitReport};

            return new ReportDto(totals, reports);
        }

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

    // TODO: get rid of duplicates
    private BigDecimal[] calcOperProfit(Map<Integer, BigDecimal[]> items) {
        BigDecimal[] operProfits = new BigDecimal[12];
        Arrays.fill(operProfits, BigDecimal.ZERO);

        items.entrySet().stream()
                .filter(transaction -> (transaction.getKey() > 0 && transaction.getKey() < 21))
                .forEach(transaction -> {
                    for (int i = 0; i < operProfits.length; i++) {
                        BigDecimal profit = transaction.getValue()[i] != null ? transaction.getValue()[i] : BigDecimal.ZERO;
                        operProfits[i] = operProfits[i].add(profit);
                    }
                });

        items.entrySet().stream()
                .filter(transaction -> (transaction.getKey() > 26 && transaction.getKey() < 40))
                .forEach(transaction -> {
                    for (int i = 0; i < operProfits.length; i++) {
                        BigDecimal expense = transaction.getValue()[i] != null ? transaction.getValue()[i] : BigDecimal.ZERO;
                        operProfits[i] = operProfits[i].subtract(expense);
                    }
                });

        return operProfits;
    }

    private BigDecimal[] calcEbitda(Map<Integer, BigDecimal[]> transactions) {
        BigDecimal[] operProfits = new BigDecimal[12];
        Arrays.fill(operProfits, BigDecimal.ZERO);

        transactions.entrySet().stream()
                .filter(transaction ->
                        (transaction.getKey() > 0 && transaction.getKey() < 26) ||
                                (transaction.getKey() > 48 && transaction.getKey() < 61) ||
                                (transaction.getKey() > 63 && transaction.getKey() < 67)
                )
                .forEach(transaction -> {
                    for (int i = 0; i < operProfits.length; i++) {
                        BigDecimal profit = transaction.getValue()[i] != null ? transaction.getValue()[i] : BigDecimal.ZERO;
                        operProfits[i] = operProfits[i].add(profit);
                    }
                });

        transactions.entrySet().stream()
                .filter(transaction ->
                        (transaction.getKey() > 26 && transaction.getKey() < 32) ||
                                (transaction.getKey() > 32 && transaction.getKey() < 40) ||
                                (transaction.getKey() > 39 && transaction.getKey() < 49) ||
                                (transaction.getKey() > 66 && transaction.getKey() < 70)
                )
                .forEach(transaction -> {
                    for (int i = 0; i < operProfits.length; i++) {
                        BigDecimal expense = transaction.getValue()[i] != null ? transaction.getValue()[i] : BigDecimal.ZERO;
                        operProfits[i] = operProfits[i].subtract(expense);
                    }
                });

        return operProfits;
    }

    private BigDecimal[] calcFinProfit(Map<Integer, BigDecimal[]> transactions) {
        BigDecimal[] operProfits = new BigDecimal[12];
        Arrays.fill(operProfits, BigDecimal.ZERO);

        transactions.entrySet().stream()
                .filter(transaction ->
                        (transaction.getKey() > 0 && transaction.getKey() < 26) ||
                                (transaction.getKey() > 48 && transaction.getKey() < 61) ||
                                (transaction.getKey() > 63 && transaction.getKey() < 67)
                )
                .forEach(transaction -> {
                    for (int i = 0; i < operProfits.length; i++) {
                        BigDecimal profit = transaction.getValue()[i] != null ? transaction.getValue()[i] : BigDecimal.ZERO;
                        operProfits[i] = operProfits[i].add(profit);
                    }
                });

        transactions.entrySet().stream()
                .filter(transaction ->
                        (transaction.getKey() > 26 && transaction.getKey() < 40) ||
                                (transaction.getKey() > 39 && transaction.getKey() < 49) ||
                                (transaction.getKey() > 60 && transaction.getKey() < 64) ||
                                (transaction.getKey() > 66 && transaction.getKey() < 70)
                )
                .forEach(transaction -> {
                    for (int i = 0; i < operProfits.length; i++) {
                        BigDecimal expense = transaction.getValue()[i] != null ? transaction.getValue()[i] : BigDecimal.ZERO;
                        operProfits[i] = operProfits[i].subtract(expense);
                    }
                });

        return operProfits;
    }

    private BigDecimal[] calcNetProfit(Map<Integer, BigDecimal[]> transactions) {
        BigDecimal[] operProfits = new BigDecimal[12];
        Arrays.fill(operProfits, BigDecimal.ZERO);

        transactions.entrySet().stream()
                .filter(transaction ->
                        (transaction.getKey() > 0 && transaction.getKey() < 26) ||
                                (transaction.getKey() > 48 && transaction.getKey() < 61) ||
                                (transaction.getKey() > 63 && transaction.getKey() < 67)
                )
                .forEach(transaction -> {
                    for (int i = 0; i < operProfits.length; i++) {
                        BigDecimal profit = transaction.getValue()[i] != null ? transaction.getValue()[i] : BigDecimal.ZERO;
                        operProfits[i] = operProfits[i].add(profit);
                    }
                });

        transactions.entrySet().stream()
                .filter(transaction ->
                        (transaction.getKey() > 25 && transaction.getKey() < 40) ||
                                (transaction.getKey() > 39 && transaction.getKey() < 49) ||
                                (transaction.getKey() > 60 && transaction.getKey() < 64) ||
                                (transaction.getKey() > 66 && transaction.getKey() < 70)
                )
                .forEach(transaction -> {
                    for (int i = 0; i < operProfits.length; i++) {
                        BigDecimal expense = transaction.getValue()[i] != null ? transaction.getValue()[i] : BigDecimal.ZERO;
                        operProfits[i] = operProfits[i].subtract(expense);
                    }
                });

        return operProfits;
    }

}
