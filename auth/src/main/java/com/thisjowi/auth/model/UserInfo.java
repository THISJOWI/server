package com.thisjowi.auth.model;

import lombok.Data;

@Data
public class UserInfo {
    private String id;
    private String email;
    private String[] roles;
}