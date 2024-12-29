package ru.aleksandrchistov.budget.transaction;

import jakarta.validation.constraints.Min;
import org.springframework.transaction.annotation.Transactional;
import ru.aleksandrchistov.budget.common.BaseRepository;

import java.util.List;

@Transactional(readOnly = true)
public interface TransactionRepository extends BaseRepository<Transaction> {

    List<Transaction> getAllByDepartmentId(@Min(1) int departmentId);
    List<Transaction> getAllByAccountId(@Min(1) int accountId);
    List<Transaction> getAllByDepartmentIdAndAccountId(@Min(1) int departmentId, @Min(1) int accountId);

}
