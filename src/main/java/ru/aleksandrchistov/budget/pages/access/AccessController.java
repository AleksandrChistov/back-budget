package ru.aleksandrchistov.budget.pages.access;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.aleksandrchistov.budget.common.error.NotFoundException;

import java.net.URI;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.aleksandrchistov.budget.common.validation.RestValidation.checkNew;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
@RequestMapping(value = AccessController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AccessController {

    public static final String REST_URL = "/api/accesses";
    protected final Logger log = getLogger(getClass());

    @Autowired
    private AccessRepository repository;

    @GetMapping
    public List<UserEntity> getAll(@Nullable @RequestParam String email, @Nullable @RequestParam String password) {
        log.info("getAll {} {}", email, password);
        if (email != null && password != null) {
            return repository.findByEmailAndPassword(email, password);
        }
        return repository.findAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserEntity> create(@Valid @RequestBody UserEntity user) {
        log.info("create {}", user);
        checkNew(user);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        UserEntity created = repository.save(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL)
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        log.info("delete {}", id);
        if (repository.delete(id) == 0) {
            throw new NotFoundException("Entity with id=" + id + " not found");
        }
    }

}
