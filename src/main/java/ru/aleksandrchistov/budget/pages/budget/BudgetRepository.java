package ru.aleksandrchistov.budget.pages.budget;

import jakarta.validation.constraints.Min;
import org.springframework.transaction.annotation.Transactional;
import ru.aleksandrchistov.budget.common.BaseRepository;
import ru.aleksandrchistov.budget.pages.budget.model.Budget;

import java.util.List;

@Transactional(readOnly = true)
public interface BudgetRepository extends BaseRepository<Budget> {
    List<Budget> getAllByDepartmentId(@Min(1) Integer departmentId);
}
