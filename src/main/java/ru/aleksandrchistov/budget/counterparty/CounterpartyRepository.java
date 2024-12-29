package ru.aleksandrchistov.budget.counterparty;

import org.springframework.transaction.annotation.Transactional;
import ru.aleksandrchistov.budget.common.BaseRepository;

@Transactional(readOnly = true)
public interface CounterpartyRepository extends BaseRepository<Counterparty> {
}
