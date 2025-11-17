package uk.thisjowi.Authentication.model;

import lombok.Data;

@Data
public class UserInfo {
    private String id;
    private String username;
    private String email;
    private String[] roles;
}