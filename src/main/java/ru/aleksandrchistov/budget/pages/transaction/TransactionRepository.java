package ru.aleksandrchistov.budget.pages.transaction;

import jakarta.validation.constraints.Min;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.aleksandrchistov.budget.common.BaseRepository;

import java.util.List;

@Transactional(readOnly = true)
public interface TransactionRepository extends BaseRepository<Transaction> {

    List<Transaction> getAllByDepartmentId(@Min(1) int departmentId);
    List<Transaction> getAllByAccountId(@Min(1) int accountId);
    List<Transaction> getAllByDepartmentIdAndAccountId(@Min(1) int departmentId, @Min(1) int accountId);

    @Query("SELECT t FROM Transaction t WHERE extract(year from t.paymentDate) = :year AND t.budgetItem.id BETWEEN :from AND :to ORDER BY t.budgetItem.id desc")
    List<Transaction> getAllByBudgetItemIdBetween(int year, @Min(1) int from, @Min(1) int to);

    @Query("SELECT t FROM Transaction t WHERE extract(year from t.paymentDate) = :year ORDER BY t.budgetItem.id desc")
    List<Transaction> findAllByPaymentDateYear(int year);

    @Query("SELECT t FROM Transaction t WHERE extract(year from t.paymentDate) = :year AND t.departmentId = :departmentId ORDER BY t.budgetItem.id desc")
    List<Transaction> findAllByPaymentDateYearAndDepartmentId(int year, @Min(1) Integer departmentId);
}
