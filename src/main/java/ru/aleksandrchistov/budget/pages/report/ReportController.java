package ru.aleksandrchistov.budget.pages.report;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.aleksandrchistov.budget.pages.budget.BudgetPlanRepository;
import ru.aleksandrchistov.budget.pages.budget.model.BudgetMonth;
import ru.aleksandrchistov.budget.pages.report.dto.ReportDto;
import ru.aleksandrchistov.budget.pages.transaction.Transaction;
import ru.aleksandrchistov.budget.pages.transaction.TransactionRepository;
import ru.aleksandrchistov.budget.pages.transaction.TransactionType;

import java.math.BigDecimal;
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
    public ReportDto getAll(
            @RequestParam TransactionType type,
            @RequestParam int year,
            @RequestParam Integer budgetId,
            @Nullable @RequestParam Integer departmentId
    ) {
        log.info("getAll {}, {}, {}, {}", type, year, budgetId, departmentId);
        List<Transaction> transactions;

        if (departmentId != null) {
            transactions = transactionRepository.findAllByPaymentDateYearAndDepartmentId(year, departmentId);
        } else {
            transactions = transactionRepository.findAllByPaymentDateYear(year);
        }

        Map<Integer, BigDecimal[]> transactionMonths = getTransactionMonths(transactions);

        List<BudgetMonth> planMoths = planRepository.getAllByBudgetId(budgetId);
        Map<Integer, BigDecimal[]> planDtos = getPlanMonths(planMoths);

        if (type == TransactionType.INCOME) {
            String[] titles = new String[]{"Операционная прибыль", "EBITDA", "Прибыль до налогов", "Чистая прибыль"};
            BigDecimal[][] plans = calcProfits(planDtos);
            BigDecimal[][] actuals = calcProfits(transactionMonths);
            return getReportDto(titles, plans, actuals);
        }

        String[] titles = new String[]{"Операционные расходы", "Оплата труда", "Налоги", "Проценты к уплате"};
        BigDecimal[][] plans = calcExpenses(planDtos);
        BigDecimal[][] actuals = calcExpenses(transactionMonths);
        return getReportDto(titles, plans, actuals);
    }

}
