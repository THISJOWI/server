package uk.thisjowi.OTP.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration events received from Authentication service
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
