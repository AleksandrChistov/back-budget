package ru.aleksandrchistov.budget.budget;

import jakarta.validation.constraints.Min;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.aleksandrchistov.budget.budget.model.BudgetMonth;
import ru.aleksandrchistov.budget.common.BaseRepository;

import java.util.List;

@Transactional(readOnly = true)
public interface BudgetPlanRepository extends BaseRepository<BudgetMonth> {

    @Query("SELECT b FROM BudgetMonth b WHERE b.budgetId = :budgetId ORDER BY b.budgetItem.id desc, b.index desc")
    List<BudgetMonth> getAllByBudgetIdOrderByBudgetItemId(@Min(1) Integer budgetId);
    List<BudgetMonth> getAllByBudgetId(@Min(1) Integer budgetId);
    void deleteByBudgetId(@Min(1) Integer budgetId);

}
