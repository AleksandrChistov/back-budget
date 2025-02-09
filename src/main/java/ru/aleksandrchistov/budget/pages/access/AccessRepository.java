package ru.aleksandrchistov.budget.pages.access;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Repository;
import ru.aleksandrchistov.budget.common.BaseRepository;

import java.util.List;

@Repository
public interface AccessRepository extends BaseRepository<User> {
    List<User> findByEmailAndPassword(
            @Email @NotBlank @Size(max = 128) String email,
            @NotBlank @Size(max = 256) String password
    );
}
