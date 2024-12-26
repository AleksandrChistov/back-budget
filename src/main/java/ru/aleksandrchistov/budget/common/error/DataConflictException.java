package ru.aleksandrchistov.budget.common.error;

public class DataConflictException extends AppException {
    public DataConflictException(String msg) {
        super(msg);
    }
}