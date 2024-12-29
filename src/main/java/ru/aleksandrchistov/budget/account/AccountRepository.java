package ru.aleksandrchistov.budget.account;

import jakarta.validation.constraints.Min;
import org.springframework.transaction.annotation.Transactional;
import ru.aleksandrchistov.budget.common.BaseRepository;

import java.util.List;

@Transactional(readOnly = true)
public interface AccountRepository extends BaseRepository<Account> {

    List<Account> getAllByDepartmentIdOrDepartmentIdIsNull(@Min(1) int departmentId);

}
