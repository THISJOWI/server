package uk.thisjowi.Authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for password change requests.
 * Current password is optional - if provided, it will be verified for security.
 * New password must always be provided and meet strength requirements.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    // Optional: current password verification. If null, password is changed without verification.
    private String currentPassword;

    @NotBlank(message = "New password cannot be empty")
    @Size(min = 8, max = 128, message = "New password must be between 8 and 128 characters")
    private String newPassword;

    @NotBlank(message = "Password confirmation cannot be empty")
    private String confirmPassword;
}
