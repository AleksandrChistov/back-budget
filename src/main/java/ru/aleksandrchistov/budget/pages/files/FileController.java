package ru.aleksandrchistov.budget.pages.files;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.aleksandrchistov.budget.pages.budget.BudgetPlanRepository;
import ru.aleksandrchistov.budget.pages.budget.dto.BudgetDataDto;
import ru.aleksandrchistov.budget.pages.budget.model.BudgetMonth;
import ru.aleksandrchistov.budget.pages.budget_item.BudgetItem;
import ru.aleksandrchistov.budget.pages.budget_item.BudgetItemRepository;
import ru.aleksandrchistov.budget.pages.transaction.Transaction;
import ru.aleksandrchistov.budget.pages.transaction.TransactionRepository;
import ru.aleksandrchistov.budget.shared.model.BudgetType;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.aleksandrchistov.budget.pages.budget.util.GetBudgetUtility.getPlanDtos;
import static ru.aleksandrchistov.budget.pages.budget.util.GetBudgetUtility.getTransactionMonths;
import static ru.aleksandrchistov.budget.pages.files.util.FileFromExcelUtility.writeFromExcel;
import static ru.aleksandrchistov.budget.pages.files.util.FileToExcelUtility.writeToExcel;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
@RequestMapping(value = FileController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class FileController {

    static final String REST_URL = "/api/files";
    protected final Logger log = getLogger(getClass());

    @Autowired
    FileSystemStorage fileSystemStorage;

    @Autowired
    private BudgetItemRepository itemRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BudgetPlanRepository planRepository;

    @Transactional
    @PutMapping(value = "/upload/{budgetId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateBudget(@PathVariable Integer budgetId, @RequestParam("file") MultipartFile file) {
        log.info("Upload file {} to budget with ID {}", file.getOriginalFilename(), budgetId);

        List<BudgetMonth> plans = planRepository.getAllByBudgetId(budgetId);

        writeFromExcel(file, plans);

        planRepository.saveAll(plans);
    }

    @Transactional
    @GetMapping(value = "/download", produces = MediaType.TEXT_PLAIN_VALUE)
    public String download(@RequestParam Integer budgetId, @RequestParam BudgetType type) {
        log.info("Download file from budget with ID: {}", budgetId);

        List<BudgetItem> items = itemRepository.getAllByTypeOrderByIdDesc(type);
        List<Transaction> transactions  = transactionRepository.getAllByBudgetItemIdBetween(
                LocalDate.now().getYear(),
                items.getLast().getId(),
                items.getFirst().getId()
        );
        HashMap<Integer, BigDecimal[]> transactionMonths = getTransactionMonths(transactions);

        List<BudgetMonth> plans = planRepository.getAllByBudgetIdOrderByBudgetItemId(budgetId);
        HashMap<Integer, BudgetDataDto> planDtos = getPlanDtos(plans, transactionMonths);

        Path dfile = fileSystemStorage.copyFile(getTemplateNameByBudgetType(type));

        writeToExcel(dfile, planDtos);

        return getPathToFile(dfile);
    }

    private static String getPathToFile(Path dfile) {
        return "http://localhost:8080/uploads/" + dfile.getFileName();
    }

    private String getTemplateNameByBudgetType(BudgetType type) {
        switch (type) {
            case EXPENSES -> {
                return "template_expenses";
            }
            case REVENUE -> {
                return "template_revenue";
            }
            case CAPEX -> {
                return "template_capex";
            }
            case CAPITAL -> {
                return "template_capital";
            }
            default -> {
                return "not_found_file";
            }
        }
    }

}
