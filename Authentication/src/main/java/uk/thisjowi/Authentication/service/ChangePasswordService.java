package uk.thisjowi.Authentication.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uk.thisjowi.Authentication.dto.ChangePasswordRequest;
import uk.thisjowi.Authentication.entity.User;
import uk.thisjowi.Authentication.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for handling password change operations.
 * Validates current password, verifies new password confirmation, and updates user password.
 */
@Service
public class ChangePasswordService {

    private static final Logger log = LoggerFactory.getLogger(ChangePasswordService.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public ChangePasswordService(UserService userService,
                                 PasswordEncoder passwordEncoder,
                                 JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Change password for a user authenticated via JWT token.
     * 
     * @param token JWT token
     * @param request ChangePasswordRequest containing new password (current password is optional)
     * @return Map with success status and message
     * @throws IllegalArgumentException if token is invalid or password change fails
     */
    public Map<String, Object> changePassword(String token, ChangePasswordRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Extract userId from token - this is more reliable than username
        Long userId = jwtUtil.extractUserId(token);
        if (userId == null) {
            log.warn("Failed to extract userId from token");
            throw new IllegalArgumentException("Invalid token");
        }

        // Validate that new password and confirmation match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.warn("Password confirmation mismatch for user ID: {}", userId);
            response.put("success", false);
            response.put("message", "New password and confirmation do not match");
            return response;
        }

        try {
            // Get user by ID (most reliable method)
            User user = userService.getUserById(userId);
            
            String username = user.getUsername();
            log.info("Processing password change for user: {} (ID: {})", username, userId);

            // If current password is provided, verify it
            if (request.getCurrentPassword() != null && !request.getCurrentPassword().isEmpty()) {
                log.debug("Verifying current password for user: {}", username);
                
                // Validate new password is not the same as current password
                if (request.getCurrentPassword().equals(request.getNewPassword())) {
                    log.warn("User {} tried to use same password for new password", username);
                    response.put("success", false);
                    response.put("message", "New password cannot be the same as current password");
                    return response;
                }
                
                // Verify current password using PasswordEncoder
                if (user.getPassword() == null || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                    log.warn("Current password mismatch for user: {}", username);
                    response.put("success", false);
                    response.put("message", "Current password is incorrect");
                    return response;
                }
            } else {
                log.info("Password change without current password verification for user: {}", username);
            }

            // Update password using dedicated method to avoid detached entity issues
            String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
            userService.updateUserPassword(userId, encodedNewPassword);

            log.info("Password successfully changed for user: {}", username);
            response.put("success", true);
            response.put("message", "Password changed successfully");
            return response;

        } catch (Exception e) {
            log.error("Unexpected error during password change for user ID {}: {}", userId, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "An error occurred while changing password");
            return response;
        }
    }

    /**
     * Validate password strength.
     * 
     * @param password Password to validate
     * @return Map with validation result and error messages if any
     */
    public Map<String, Object> validatePasswordStrength(String password) {
        Map<String, Object> result = new HashMap<>();
        result.put("isValid", true);

        if (password == null || password.length() < 8) {
            result.put("isValid", false);
            result.put("error", "Password must be at least 8 characters long");
            return result;
        }

        if (password.length() > 128) {
            result.put("isValid", false);
            result.put("error", "Password must not exceed 128 characters");
            return result;
        }

        // Check password character requirements using linear iteration (prevents ReDoS)
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        String specialChars = "!@#$%^&*()_+-=[]{}; ':\"\\|,.<>?/";
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (specialChars.indexOf(c) >= 0) hasSpecial = true;
        }
        
        if (!hasUpper) {
            result.put("isValid", false);
            result.put("error", "Password must contain at least one uppercase letter");
            return result;
        }
        
        if (!hasLower) {
            result.put("isValid", false);
            result.put("error", "Password must contain at least one lowercase letter");
            return result;
        }
        
        if (!hasDigit) {
            result.put("isValid", false);
            result.put("error", "Password must contain at least one digit");
            return result;
        }
        
        if (!hasSpecial) {
            result.put("isValid", false);
            result.put("error", "Password must contain at least one special character (!@#$%^&*...)");
            return result;
        }

        log.debug("Password validation passed for strength check");
        return result;
    }
}
