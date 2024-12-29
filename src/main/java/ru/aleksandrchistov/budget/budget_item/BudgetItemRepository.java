package ru.aleksandrchistov.budget.budget_item;

import org.springframework.transaction.annotation.Transactional;
import ru.aleksandrchistov.budget.common.BaseRepository;
import ru.aleksandrchistov.budget.shared.model.BudgetType;

import java.util.List;

@Transactional(readOnly = true)
public interface BudgetItemRepository extends BaseRepository<BudgetItem> {

    List<BudgetItem> getAllByType(BudgetType type);

}
