package ru.aleksandrchistov.budget.counterparty;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
@RequestMapping(value = CounterpartyController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class CounterpartyController {

    static final String REST_URL = "/api/counterparty";
    protected final Logger log = getLogger(getClass());

    @Autowired
    private CounterpartyRepository repository;

    @GetMapping
    public List<Counterparty> getAll() {
        log.info("getAll");
        return repository.findAll();
    }

}
