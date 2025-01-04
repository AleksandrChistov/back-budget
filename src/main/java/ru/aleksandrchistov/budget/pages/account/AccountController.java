package ru.aleksandrchistov.budget.pages.account;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
@RequestMapping(value = AccountController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {

    static final String REST_URL = "/api/accounts";
    protected final Logger log = getLogger(getClass());

    @Autowired
    private AccountRepository repository;

    @GetMapping
    public List<Account> getAll(@Nullable @RequestParam Integer departmentId) {
        log.info("getAll");
        if (departmentId != null) {
            return repository.getAllByDepartmentIdOrDepartmentIdIsNull(departmentId);
        }
        return repository.findAll();
    }

}
