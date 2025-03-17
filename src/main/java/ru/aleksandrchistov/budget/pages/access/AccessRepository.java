package ru.aleksandrchistov.budget.pages.access;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Repository;
import ru.aleksandrchistov.budget.common.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessRepository extends BaseRepository<UserEntity> {
    List<UserEntity> findByEmailAndPassword(
            @Email @NotBlank @Size(max = 128) String email,
            @NotBlank @Size(max = 256) String password
    );
    Optional<UserEntity> findByEmail(String username);
}
