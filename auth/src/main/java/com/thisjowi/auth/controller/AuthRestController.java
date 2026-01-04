package com.thisjowi.auth.controller;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.thisjowi.auth.entity.Deployment;
import com.thisjowi.auth.entity.Account;
import com.thisjowi.auth.repository.UserRepository;
import com.thisjowi.auth.service.UserService;
import com.thisjowi.auth.service.ChangePasswordService;
import com.thisjowi.auth.service.EmailService;
import com.thisjowi.auth.utils.JwtUtil;
import com.thisjowi.auth.dto.ChangePasswordRequest;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.thisjowi.auth.entity.User;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ChangePasswordService changePasswordService;
    private final EmailService emailService;
    private final Logger log = LoggerFactory.getLogger(AuthRestController.class);

    public AuthRestController(AuthenticationManager authenticationManager,
                              UserRepository userRepository, PasswordEncoder passwordEncoder,
                              UserService userService, JwtUtil jwtUtil,
                              ChangePasswordService changePasswordService,
                              EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.changePasswordService = changePasswordService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> apiLogin(@RequestBody Map<String, String> body, HttpServletRequest request) {
        log.info("Login attempt received from: {}", request.getRemoteAddr());
        
        // Use email as primary identifier
        String identifierRaw = body.get("email");
        final String identifier = (identifierRaw != null) ? identifierRaw.trim() : null;
        String passwordRaw = body.get("password");
        final String password = (passwordRaw != null) ? passwordRaw.trim() : null;

        if (identifier == null || identifier.isEmpty() || password == null || password.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Missing or empty email or password"));
        }

        try {
            var token = new UsernamePasswordAuthenticationToken(identifier, password);
            var auth = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            // Create session if necessary
            request.getSession(true);

            // Get user details including ID
            var user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Update last login
            user.setLastLogin(LocalDate.now());
            userRepository.save(user);

            // generate JWT token with user ID
            String jwtToken = jwtUtil.generateToken(user.getId(), user.getEmail());
            log.info("User '{}' (ID: {}) authenticated successfully", user.getEmail(), user.getId());

            return ResponseEntity.ok(Map.of("success", true, "email", user.getEmail(), "token", jwtToken));
        } catch (AuthenticationException ex) {
            log.warn("Authentication failed for identifier {}: {}", identifier, ex.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", "Invalid credentials"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> apiRegister(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String fullName = body.get("fullName");
        String country = body.get("country");
        String birthdateStr = body.get("birthdate");
        String accountTypeStr = body.get("accountType");
        String deploymentTypeStr = body.get("hostingMode");

        if (password == null || email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Missing email or password"));
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("success", false, "message", "Email already exists"));
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        
        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName.trim());
        } else {
            // Fallback or error? Since it is nullable=false in DB, we should probably require it or set a default.
            // For now, let's set it to empty string or part of email if missing to avoid DB error, 
            // but ideally the frontend should send it.
            // Given the user just asked to "add" it, I'll assume they will start sending it.
            // Let's default to "User" or extract from email if not provided to be safe.
            user.setFullName(email.split("@")[0]);
        }
        
        if (country != null && !country.trim().isEmpty()) {
            user.setCountry(country.trim());
        }
        
        if (birthdateStr != null && !birthdateStr.trim().isEmpty()) {
            try {
                user.setBirthdate(LocalDate.parse(birthdateStr.trim()));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Invalid birthdate format. Use yyyy-MM-dd"));
            }
        }

        if (accountTypeStr != null && !accountTypeStr.trim().isEmpty()) {
            try {
                // Map frontend value to enum
                // Frontend sends "personal" or "business" usually, but let's check what it sends
                // Assuming it sends values matching the enum or we need to map them
                // Enum: Buisiness, Community
                // Let's default to Community if not specified or invalid for now, or try to parse
                // Case insensitive matching
                for (Account acc : Account.values()) {
                    if (acc.name().equalsIgnoreCase(accountTypeStr.trim())) {
                        user.setAccountType(acc);
                        break;
                    }
                }
            } catch (Exception e) {
                log.warn("Invalid account type: {}", accountTypeStr);
            }
        }
        if (user.getAccountType() == null) {
             user.setAccountType(Account.Community); // Default
        }

        if (deploymentTypeStr != null && !deploymentTypeStr.trim().isEmpty()) {
             try {
                // Enum: SelfHosted, Cloud
                // Frontend sends "self-hosted" or "cloud"
                String normalized = deploymentTypeStr.trim().replace("-", "");
                for (Deployment dep : Deployment.values()) {
                    if (dep.name().equalsIgnoreCase(normalized)) {
                        user.setDeploymentType(dep);
                        break;
                    }
                }
            } catch (Exception e) {
                log.warn("Invalid deployment type: {}", deploymentTypeStr);
            }
        }
        if (user.getDeploymentType() == null) {
            user.setDeploymentType(Deployment.Cloud); // Default
        }

        // Generate verification code
        String verificationCode = generateVerificationCode();
        user.setVerificationCode(verificationCode);
        user.setVerified(false);

        user.setLastLogin(LocalDate.now());
        user = userService.saveUser(user);

        // Send verification email
        try {
            emailService.sendVerificationEmail(user.getEmail(), verificationCode);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}", user.getEmail(), e);
            // We don't fail registration if email fails, but user might need to resend
        }

        // Generate token on register as well so clients can use it immediately
        String jwtToken = jwtUtil.generateToken(user.getId(), user.getEmail());
        log.info("Registered new user '{}' (ID: {})", user.getEmail(), user.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "email", user.getEmail(), "token", jwtToken));
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");

        if (email == null || code == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email and code are required"));
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isVerified()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User already verified"));
        }

        if (code.equals(user.getVerificationCode())) {
            user.setVerified(true);
            user.setVerificationCode(null); // Clear code after successful verification
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("success", true, "message", "Email verified successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid verification code"));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email is required"));
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isVerified()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User already verified"));
        }

        String verificationCode = generateVerificationCode();
        user.setVerificationCode(verificationCode);
        userRepository.save(user);

        try {
            emailService.sendVerificationEmail(user.getEmail(), verificationCode);
            return ResponseEntity.ok(Map.of("success", true, "message", "Verification email sent"));
        } catch (Exception e) {
            log.error("Failed to send verification email to {}", user.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to send email"));
        }
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

    @PutMapping("/user")
    public ResponseEntity<?> updateUser(
            @RequestBody Map<String, String> body,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        
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
            User user = userService.getUserById(userId);
            
            String country = body.get("country");
            String birthdateStr = body.get("birthdate");
            String accountTypeStr = body.get("accountType");
            String deploymentTypeStr = body.get("hostingMode");

            if (country != null) {
                user.setCountry(country.trim());
            }
            
            if (birthdateStr != null && !birthdateStr.trim().isEmpty()) {
                try {
                    user.setBirthdate(LocalDate.parse(birthdateStr.trim()));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("success", false, "message", "Invalid birthdate format. Use yyyy-MM-dd"));
                }
            }

            if (accountTypeStr != null && !accountTypeStr.trim().isEmpty()) {
                try {
                    for (Account acc : Account.values()) {
                        if (acc.name().equalsIgnoreCase(accountTypeStr.trim())) {
                            user.setAccountType(acc);
                            break;
                        }
                    }
                } catch (Exception e) {
                    log.warn("Invalid account type: {}", accountTypeStr);
                }
            }

            if (deploymentTypeStr != null && !deploymentTypeStr.trim().isEmpty()) {
                 try {
                    String normalized = deploymentTypeStr.trim().replace("-", "");
                    for (Deployment dep : Deployment.values()) {
                        if (dep.name().equalsIgnoreCase(normalized)) {
                            user.setDeploymentType(dep);
                            break;
                        }
                    }
                } catch (Exception e) {
                    log.warn("Invalid deployment type: {}", deploymentTypeStr);
                }
            }

            userService.saveUser(user);
            log.info("User details updated for user ID: {}", userId);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "User details updated successfully"));
            
        } catch (Exception e) {
            log.error("Error updating user details for ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error updating user details"));
        }
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

    @GetMapping("/country/{userId}")
    public ResponseEntity<?> getUserCountry(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return ResponseEntity.ok(Map.of("country", user.getCountry()));
    }

    @PostMapping("/country/{userId}")
    public ResponseEntity<?> setUserCountry(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        String country = body.get("country");
        if (country == null || country.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Country cannot be empty"));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        user.setCountry(country);
        userService.saveUser(user);
        log.info("Set country '{}' for user ID {}", country, userId);
        return ResponseEntity.ok(Map.of("success", true, "country", country));
    }

    @GetMapping("/user-details/{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/user-by-email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            User user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/birthdate/{userId}")
    public ResponseEntity<?> getUserBirthdate(@PathVariable Long userId) {
        try {
            LocalDate birthdate = userService.getUserBirthdate(userId);
            return ResponseEntity.ok(Map.of("birthdate", birthdate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/birthdate/{userId}")
    public ResponseEntity<?> setUserBirthdate(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        String birthdateStr = body.get("birthdate");
        if (birthdateStr == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", "Birthdate is required"));
        }
        try {
            LocalDate birthdate = LocalDate.parse(birthdateStr);
            userService.setUserBirthdate(userId, birthdate);
            return ResponseEntity.ok(Map.of("success", true, "birthdate", birthdate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", "Invalid date format or user not found"));
        }
    }

    @GetMapping("/deployment-type/{userId}")
    public ResponseEntity<?> getUserDeployment(@PathVariable Long userId) {
        try {
            Deployment deployment = userService.getDeploymentType(userId);
            return ResponseEntity.ok(Map.of("deployment", deployment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/deployment-type/{userId}")
    public ResponseEntity<?> setUserDeployment(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        String deploymentStr = body.get("deployment");
        if (deploymentStr == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", "Deployment type is required"));
        }
        try {
            Deployment deployment = Deployment.valueOf(deploymentStr);
            userService.setDeploymentType(userId, deployment);
            return ResponseEntity.ok(Map.of("success", true, "deployment", deployment));
        } catch (IllegalArgumentException e) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", "Invalid deployment type"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/account-type/{userId}")
    public ResponseEntity<?> getUserAccountType(@PathVariable Long userId) {
        try {
            Account accountType = userService.getAccountType(userId);
            return ResponseEntity.ok(Map.of("accountType", accountType));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/account-type/{userId}")
    public ResponseEntity<?> setUserAccountType(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        String accountTypeStr = body.get("accountType");
        if (accountTypeStr == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", "Account type is required"));
        }
        try {
            Account accountType = Account.valueOf(accountTypeStr);
            userService.setAccountType(userId, accountType);
            return ResponseEntity.ok(Map.of("success", true, "accountType", accountType));
        } catch (IllegalArgumentException e) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", "Invalid account type"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/createdat/{userId}")
    public ResponseEntity<?> getUserCreatedAt(@PathVariable Long userId) {
        try {
            LocalDateTime createdAt = userService.getAccountCreationDate(userId);
            return ResponseEntity.ok(Map.of("createdAt", createdAt));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

}

