package ru.aleksandrchistov.budget.common.error;

public class IllegalRequestDataException extends AppException {
    public IllegalRequestDataException(String msg) {
        super(msg);
    }
}