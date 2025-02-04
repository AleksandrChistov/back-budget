package ru.aleksandrchistov.budget.pages.department;

import org.springframework.transaction.annotation.Transactional;
import ru.aleksandrchistov.budget.common.BaseRepository;

@Transactional(readOnly = true)
public interface DepartmentRepository extends BaseRepository<Department> {
}
