package uk.thisjowi.OTP.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for OTP creation events to send to other services
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpCreatedEvent {
    private Long otpId;
    private Long userId;
    private String username;
    private String type; // TOTP, HOTP
    private String eventType; // "OTP_CREATED"
    private Long timestamp;
    private Long expiresAt;
}
