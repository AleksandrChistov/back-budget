package ru.aleksandrchistov.budget.budget;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.transaction.annotation.Transactional;
import ru.aleksandrchistov.budget.common.BaseRepository;
import ru.aleksandrchistov.budget.shared.model.BudgetType;

import java.util.List;

@Transactional(readOnly = true)
public interface BudgetRepository extends BaseRepository<Budget> {

    List<Budget> getAllByType(@NotNull @Size(max = 10) BudgetType type);

    List<Budget> getAllByTypeAndDepartmentId(@NotNull @Size(max = 10) BudgetType type, @Min(1) Integer departmentId);

    List<Budget> getAllByTypeAndId(@NotNull BudgetType type, Integer id);

}
