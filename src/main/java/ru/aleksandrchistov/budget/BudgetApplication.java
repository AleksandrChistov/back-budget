package ru.aleksandrchistov.budget;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.aleksandrchistov.budget.properties.FileTemplateProperties;
import ru.aleksandrchistov.budget.properties.FileUploadProperties;

@SpringBootApplication
@EnableConfigurationProperties({FileUploadProperties.class, FileTemplateProperties.class})
public class BudgetApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetApplication.class, args);
    }

}
