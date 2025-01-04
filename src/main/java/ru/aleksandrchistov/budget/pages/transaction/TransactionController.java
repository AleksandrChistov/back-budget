package ru.aleksandrchistov.budget.pages.transaction;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.aleksandrchistov.budget.pages.account.Account;
import ru.aleksandrchistov.budget.pages.account.AccountRepository;
import ru.aleksandrchistov.budget.pages.budget_item.BudgetItem;
import ru.aleksandrchistov.budget.pages.budget_item.BudgetItemRepository;
import ru.aleksandrchistov.budget.common.error.NotFoundException;
import ru.aleksandrchistov.budget.pages.counterparty.Counterparty;
import ru.aleksandrchistov.budget.pages.counterparty.CounterpartyRepository;

import java.net.URI;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.aleksandrchistov.budget.common.validation.RestValidation.checkNew;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
@RequestMapping(value = TransactionController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {

    static final String REST_URL = "/api/transactions";
    protected final Logger log = getLogger(getClass());

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BudgetItemRepository budgetItemRepository;

    @Autowired
    private CounterpartyRepository counterpartyRepository;

    @GetMapping
    public List<Transaction> getAll(@Nullable @RequestParam Integer departmentId, @Nullable @RequestParam Integer accountId) {
        log.info("getAll");
        if (departmentId != null && accountId != null) {
            return repository.getAllByDepartmentIdAndAccountId(departmentId, accountId);
        } else if (departmentId != null) {
            return repository.getAllByDepartmentId(departmentId);
        } else if (accountId != null) {
            return repository.getAllByAccountId(accountId);
        }
        return repository.findAll();
    }

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Transaction> create(@Valid @RequestBody TransactionDto transactionDto) {
        log.info("create {}", transactionDto);
        Transaction transaction = getTransactionFromDto(transactionDto);
        checkNew(transaction);
        Transaction created = repository.save(transaction);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL)
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        log.info("delete {}", id);
        if (repository.delete(id) == 0) {
            throw new NotFoundException("Entity with id=" + id + " not found");
        }
    }

    private Transaction getTransactionFromDto(TransactionDto dto) {
        Account account = accountRepository.getExisted(dto.getAccountId());
        BudgetItem budgetItem = budgetItemRepository.getExisted(dto.getBudgetItemId());
        Counterparty counterparty = counterpartyRepository.getExisted(dto.getCounterpartyId());
        return new Transaction(
                null, dto.getSum(), dto.getType(), dto.getPaymentDate(),
                dto.getDescription(), account, budgetItem, counterparty, dto.getDepartmentId()
        );
    }

}
