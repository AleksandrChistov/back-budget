package ru.aleksandrchistov.budget.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {
    private String location;
}
