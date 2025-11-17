package uk.thisjowi.Authentication.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import uk.thisjowi.Authentication.repository.UserRepository;
import uk.thisjowi.Authentication.service.UserService;
import uk.thisjowi.Authentication.service.ChangePasswordService;
import uk.thisjowi.Authentication.utils.JwtUtil;
import uk.thisjowi.Authentication.dto.ChangePasswordRequest;

import org.springframework.security.crypto.password.PasswordEncoder;
import uk.thisjowi.Authentication.entity.User;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ChangePasswordService changePasswordService;
    private final Logger log = LoggerFactory.getLogger(AuthRestController.class);

    public AuthRestController(AuthenticationManager authenticationManager,
                              UserRepository userRepository, PasswordEncoder passwordEncoder,
                              UserService userService, JwtUtil jwtUtil,
                              ChangePasswordService changePasswordService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.changePasswordService = changePasswordService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> apiLogin(@RequestBody Map<String, String> body, HttpServletRequest request) {
        log.info("Login attempt received from: {}", request.getRemoteAddr());
        
        String usernameRaw = body.getOrDefault("username", body.get("email"));
        final String username = (usernameRaw != null) ? usernameRaw.trim() : null;
        String passwordRaw = body.get("password");
        final String password = (passwordRaw != null) ? passwordRaw.trim() : null;

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Missing or empty username/email or password"));
        }

        try {
            var token = new UsernamePasswordAuthenticationToken(username, password);
            var auth = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            // Create session if necessary
            request.getSession(true);

            // Get user details including ID
            var user = userRepository.findByUsername(username)
                    .orElseGet(() -> userRepository.findByEmail(username)
                            .orElseThrow(() -> new RuntimeException("User not found")));

            // generate JWT token with user ID
            String jwtToken = jwtUtil.generateToken(user.getId(), username);
            log.info("User '{}' (ID: {}) authenticated successfully", username, user.getId());

            return ResponseEntity.ok(Map.of("success", true, "username", username, "token", jwtToken));
        } catch (AuthenticationException ex) {
            log.warn("Authentication failed for user {}: {}", username, ex.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", "Invalid credentials"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> apiRegister(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");

        if (username == null || password == null || email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Missing username, email or password"));
        }

        if (userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("success", false, "message", "User or email already exists"));
        }

    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    user = userService.saveUser(user);

        // Generate token on register as well so clients can use it immediately
        String jwtToken = jwtUtil.generateToken(user.getId(), username);
        log.info("Registered new user '{}' (ID: {})", username, user.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "username", username, "token", jwtToken));
    }

    @GetMapping("/user")
    @ResponseBody
    public ResponseEntity<?> getUserFromToken(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);
        if (userId == null || userId == -1L) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Invalid or expired token"));
        }

        // Return the userId directly from the token (no need to query database)
        return ResponseEntity.ok(userId);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);
        
        try {
            // Validate password strength
            Map<String, Object> strengthValidation = changePasswordService.validatePasswordStrength(request.getNewPassword());
            if (!(boolean) strengthValidation.get("isValid")) {
                log.warn("Password strength validation failed: {}", strengthValidation.get("error"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "success", false,
                                "message", (String) strengthValidation.get("error")
                        ));
            }

            // Change password using the service
            Map<String, Object> result = changePasswordService.changePassword(token, request);
            
            if ((boolean) result.get("success")) {
                log.info("Password changed successfully for authenticated user");
                return ResponseEntity.ok(result);
            } else {
                log.warn("Password change failed: {}", result.get("message"));
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
            }
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid token during password change: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", "Invalid token"));
        } catch (Exception e) {
            log.error("Unexpected error during password change: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "An error occurred while changing password"));
        }
    }

    @PostMapping("/validate-password-strength")
    public ResponseEntity<?> validatePasswordStrength(@RequestBody Map<String, String> body) {
        String password = body.get("password");
        
        if (password == null || password.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Password cannot be empty"));
        }
        
        try {
            Map<String, Object> result = changePasswordService.validatePasswordStrength(password);
            
            if ((boolean) result.get("isValid")) {
                log.debug("Password strength validation passed");
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Password is strong and meets all requirements"
                ));
            } else {
                log.debug("Password strength validation failed: {}", result.get("error"));
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", (String) result.get("error")
                ));
            }
        } catch (Exception e) {
            log.error("Error validating password strength: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error validating password"));
        }
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", "Invalid token"));
        }

        try {
            userService.deleteUserById(userId);
            log.info("Account deleted successfully for user ID: {}", userId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Account deleted successfully"));
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";
            log.error("Error deleting account for user ID {}: {}", userId, errorMessage, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error deleting account: " + errorMessage));
        }
    }
}