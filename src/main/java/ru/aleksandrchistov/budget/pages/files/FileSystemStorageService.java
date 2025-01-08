package ru.aleksandrchistov.budget.pages.files;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.aleksandrchistov.budget.properties.FileTemplateProperties;
import ru.aleksandrchistov.budget.properties.FileUploadProperties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileSystemStorageService implements FileSystemStorage {
    private final Path dirUpload;
    private final Path dirTemplate;

    @Autowired
    public FileSystemStorageService(FileUploadProperties fileUploadProperties, FileTemplateProperties fileTemplateProperties) {
        this.dirUpload = Paths.get(fileUploadProperties.getLocation())
                .toAbsolutePath()
                .normalize();
        this.dirTemplate = Paths.get(fileTemplateProperties.getLocation())
                .toAbsolutePath()
                .normalize();
    }

    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.dirUpload);
        }
        catch (Exception ex) {
            throw new RuntimeException("Could not initialize storage location", ex);
        }
    }

    @Override
    public Path copyFile(String fileName) {
        try {
            Path dtemplate = this.dirTemplate.resolve(fileName + ".xlsx");
            Path dfile = this.dirUpload.resolve(fileName + ".xlsx");
            Files.copy(dtemplate, dfile, StandardCopyOption.REPLACE_EXISTING);
            return dfile;

        } catch (Exception e) {
            throw new RuntimeException("Could not upload file");
        }
    }
}
