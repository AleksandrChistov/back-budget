package ru.aleksandrchistov.budget.access;

import org.springframework.stereotype.Repository;
import ru.aleksandrchistov.budget.common.BaseRepository;

@Repository
public interface AccessRepository extends BaseRepository<User> {
}
