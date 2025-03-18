package ru.aleksandrchistov.budget.common.auth;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String username;
    private String role;
}
