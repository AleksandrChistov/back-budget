package ru.aleksandrchistov.budget.common.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
