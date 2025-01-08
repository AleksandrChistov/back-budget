package ru.aleksandrchistov.budget.pages.files;

import java.nio.file.Path;

public interface FileSystemStorage {
    void init();
    Path copyFile(String fileName);
}
