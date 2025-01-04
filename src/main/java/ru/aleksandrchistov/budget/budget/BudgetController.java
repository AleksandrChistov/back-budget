package ru.aleksandrchistov.budget.budget;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.aleksandrchistov.budget.budget.dto.BudgetDataDto;
import ru.aleksandrchistov.budget.budget.dto.BudgetDto;
import ru.aleksandrchistov.budget.budget.dto.BudgetItemDto;
import ru.aleksandrchistov.budget.budget_item.BudgetItem;
import ru.aleksandrchistov.budget.budget_item.BudgetItemRepository;
import ru.aleksandrchistov.budget.common.error.NotFoundException;
import ru.aleksandrchistov.budget.shared.model.BudgetType;
import ru.aleksandrchistov.budget.transaction.Transaction;
import ru.aleksandrchistov.budget.transaction.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.aleksandrchistov.budget.budget.CreateBudgetUtility.getPlansFromDto;
import static ru.aleksandrchistov.budget.budget.GetBudgetUtility.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
@RequestMapping(value = BudgetController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class BudgetController {

    static final String REST_URL = "/api/budgets";
    protected final Logger log = getLogger(getClass());

    @Autowired
    private BudgetRepository repository;

    @Autowired
    private BudgetItemRepository itemRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BudgetPlanRepository planRepository;

    @GetMapping(path = "/names")
    public List<Budget> getNames(@RequestParam BudgetType type, @Nullable @RequestParam Integer departmentId) {
        log.info("getNames");
        if (departmentId == null) {
            return repository.getAllByType(type);
        }
        List<Budget> budgets = repository.getAllByTypeAndDepartmentId(type, departmentId);
        if (budgets.isEmpty()) {
            return repository.getAllByTypeAndId(type, DefaultBudgetId.fromConst(type));
        }
        return budgets;
    }

    @GetMapping
    public BudgetDto getAll(@RequestParam BudgetType type, @RequestParam Integer budgetId) {
        log.info("getAll");
        List<BudgetItem> items = itemRepository.getAllByTypeOrderByIdDesc(type);
        List<Transaction> transactions  = transactionRepository.getAllByBudgetItemIdBetween(
                2024, // TODO: replace with request param in the future
                items.getLast().getId(),
                items.getFirst().getId()
        );
        HashMap<Integer, BigDecimal[]> transactionMonths = getTransactionMonths(transactions);

        List<BudgetMonth> plans = planRepository.getAllByBudgetIdOrderByBudgetItemId(budgetId);
        HashMap<Integer, BudgetDataDto> planDtos = getPlanDtos(plans, transactionMonths);

        List<BudgetItemDto> itemDtos = getItemDtos(items, planDtos);
        return budgetDto(itemDtos, type);
    }

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> create(@Valid @RequestBody BudgetDto budgetDto) {
        log.info("create {}", budgetDto);
        List<Budget> budgets = repository.getAllByTypeAndDepartmentId(budgetDto.getType(), budgetDto.getDepartmentId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        Budget newBudget = new Budget(
                null,
                "Версия №" + (budgets.size() + 1) + " от " + LocalDate.now().format(formatter),
                budgetDto.getType(),
                budgetDto.getDepartmentId()
        );

        Budget createdBudget = repository.save(newBudget);
        List<BudgetMonth> plans = getPlansFromDto(budgetDto, createdBudget.getId(), itemRepository);

        planRepository.saveAll(plans);

        return new ResponseEntity<>(newBudget.getId(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        log.info("delete {}", id);
        if (repository.delete(id) == 0) {
            throw new NotFoundException("Entity with id=" + id + " not found");
        }
        planRepository.deleteByBudgetId(id);
    }

}
