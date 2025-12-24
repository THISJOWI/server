package com.thisjowi.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String country;
    private String accountType;
    private String hostingMode;
}