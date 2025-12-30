package com.thisjowi.password.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.thisjowi.password.Entity.Password;
import com.thisjowi.password.Entity.PasswordDTO;
import com.thisjowi.password.Service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/passwords")
public class PasswordController {
    private static final Logger log = LoggerFactory.getLogger(PasswordController.class);

    @Autowired
    private PasswordService passwordService;

    @GetMapping
    public ResponseEntity<?> getPasswordsByToken(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        try {
            if (authHeader == null || authHeader.isBlank()) {
                log.warn("GET /passwords: No Authorization header provided");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authorization header required"));
            }
            
            List<Password> list = passwordService.getPasswordsByToken(authHeader);
            if (list == null) {
                log.warn("GET /passwords: Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
            }
            
            log.info("GET /passwords: Retrieved {} passwords", list.size());
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException e) {
            log.error("GET /passwords: Invalid argument - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Authentication failed"));
        } catch (Exception e) {
            log.error("GET /passwords: Unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createPassword(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @RequestBody PasswordDTO passwordDTO) {
        try {
            if (authHeader == null || authHeader.isBlank()) {
                log.warn("POST /passwords: No Authorization header provided");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authorization header required"));
            }
            
            // Validate input data
            if (passwordDTO == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Request body is required"));
            }
            if (passwordDTO.getPassword() == null || passwordDTO.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Password is required"));
            }
            if (passwordDTO.getName() == null || passwordDTO.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Title is required"));
            }
            if (passwordDTO.getWebsite() != null && !passwordDTO.getWebsite().trim().isEmpty() && 
                !passwordDTO.getWebsite().matches("^https?://")) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Website must start with http:// or https://"));
            }
            
            Password password = passwordDTO.toEntity();
            Password saved = passwordService.savePasswordForToken(authHeader, password);
            log.info("POST /passwords: Created new password");
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            log.error("POST /passwords: Invalid argument - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Authentication failed"));
        } catch (Exception e) {
            log.error("POST /passwords: Unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePassword(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody PasswordDTO passwordDTO) {
        try {
            if (authHeader == null || authHeader.isBlank()) {
                log.warn("PUT /passwords/{}: No Authorization header provided", id);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authorization header required"));
            }
            
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid password ID"));
            }
            
            if (passwordDTO == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Request body is required"));
            }
            
            Password passwordData = passwordDTO.toEntity();
            Password updated = passwordService.updatePasswordByToken(authHeader, id, passwordData);
            log.info("PUT /passwords/{}: Password updated", id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("PUT /passwords/{}: Invalid argument - {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Authentication failed or resource not found"));
        } catch (SecurityException se) {
            log.warn("PUT /passwords/{}: Forbidden - user not authorized", id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Not authorized to update this resource"));
        } catch (Exception e) {
            log.error("PUT /passwords/{}: Unexpected error", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePassword(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @PathVariable Long id) {
        try {
            if (authHeader == null || authHeader.isBlank()) {
                log.warn("DELETE /passwords/{}: No Authorization header provided", id);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authorization header required"));
            }
            
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid password ID"));
            }
            
            passwordService.deletePasswordByToken(authHeader, id);
            log.info("DELETE /passwords/{}: Password deleted", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("DELETE /passwords/{}: Invalid argument - {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Authentication failed or resource not found"));
        } catch (SecurityException se) {
            log.warn("DELETE /passwords/{}: Forbidden - user not authorized", id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Not authorized to delete this resource"));
        } catch (Exception e) {
            log.error("DELETE /passwords/{}: Unexpected error", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
        }
    }
}
