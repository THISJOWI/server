package com.thisjowi.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration events to send to other services
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    private Long userId;
    private String email;
    private String eventType; // "USER_REGISTERED"
    private Long timestamp;
}
