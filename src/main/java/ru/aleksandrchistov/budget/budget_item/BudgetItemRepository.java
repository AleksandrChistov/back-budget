package ru.aleksandrchistov.budget.budget_item;

import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import ru.aleksandrchistov.budget.common.BaseRepository;
import ru.aleksandrchistov.budget.shared.model.BudgetType;
import ru.aleksandrchistov.budget.transaction.TransactionType;

import java.util.List;

@Transactional(readOnly = true)
public interface BudgetItemRepository extends BaseRepository<BudgetItem> {

    List<BudgetItem> getAllByType(@NotNull BudgetType type);
    List<BudgetItem> getAllByTypeOrderByIdDesc(@NotNull BudgetType type);
    List<BudgetItem> getAllByTypeAndTransactionType(@NotNull BudgetType type, @NotNull TransactionType transactionType);

}
