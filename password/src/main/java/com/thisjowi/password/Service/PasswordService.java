package com.thisjowi.password.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.thisjowi.password.Entity.Password;
import com.thisjowi.password.Repository.PasswordRepository;
import com.thisjowi.password.Utils.Encryption;
import com.thisjowi.password.Utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class PasswordService {
    private static final Logger log = LoggerFactory.getLogger(PasswordService.class);

    private final PasswordRepository passwordRepository;
    private final JwtUtil jwtUtil;
    private final Encryption encryption;

    public Password savePassword(Password password) {
        // Encrypt sensitive fields before saving
        if (password.getPassword() != null && !password.getPassword().isEmpty()) {
            password.setPassword(encryption.encrypt(password.getPassword()));
        }
        if (password.getWebsite() != null && !password.getWebsite().isEmpty()) {
            password.setWebsite(encryption.encrypt(password.getWebsite()));
        }
        if (password.getName() != null && !password.getName().isEmpty()) {
            password.setName(encryption.encrypt(password.getName()));
        }
        
        Password saved = passwordRepository.save(password);
        
        // Decrypt for the returned object
        decryptPasswordFields(saved);
        return saved;
    }

    /**
     * Get all passwords for the user in the JWT token (Authorization header).
     * IMPORTANT: Only accepts Authorization header, NOT Kafka fallback (security measure).
     */
    public List<Password> getPasswordsByToken(String authHeader) {
        log.debug("Attempting to extract userId from Authorization header");
        
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            log.warn("Failed to extract userId from Authorization header");
            return null;
        }
        
        log.info("User {} requested passwords", userId);
        return getPasswordsByUserId(userId);
    }

    /**
     * Save a password for the authenticated user (Authorization header).
     */
    public Password savePasswordForToken(String authHeader, Password password) {
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null || userId == -1L) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        password.setUserId(userId);
        return savePassword(password);
    }

    /**
     * Update a password if it belongs to the authenticated user.
     */
    public Password updatePasswordByToken(String authHeader, Long id, Password passwordData) {
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null || userId == -1L) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        var opt = passwordRepository.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Password not found");
        }
        Password existing = opt.get();
        if (!userId.equals(existing.getUserId())) {
            throw new SecurityException("Not authorized to update this resource");
        }
        
        // Update and encrypt name/title field
        if (passwordData.getName() != null && !passwordData.getName().trim().isEmpty()) {
            existing.setName(encryption.encrypt(passwordData.getName().trim()));
        }
        
        // Update and encrypt password field
        if (passwordData.getPassword() != null && !passwordData.getPassword().trim().isEmpty()) {
            existing.setPassword(encryption.encrypt(passwordData.getPassword().trim()));
        }
        
        // Update and encrypt website field
        if (passwordData.getWebsite() != null && !passwordData.getWebsite().trim().isEmpty()) {
            existing.setWebsite(encryption.encrypt(passwordData.getWebsite().trim()));
        }
        
        return updatePassword(existing);
    }

    /**
     * Delete a password if it belongs to the authenticated user.
     */
    public void deletePasswordByToken(String authHeader, Long id) {
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null || userId == -1L) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        var opt = passwordRepository.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Password not found");
        }
        Password p = opt.get();
        if (!userId.equals(p.getUserId())) {
            throw new SecurityException("Not authorized to delete this resource");
        }
        passwordRepository.deleteById(id);
    }

    /**
     * Extract user ID from JWT token (Authorization header ONLY).
     * SECURITY: No longer falls back to Kafka token for HTTP requests.
     */
    private Long extractUserIdFromToken(String authHeader) {
        log.debug("Extracting userId from Authorization header");
        
        // Only accept Authorization header
        if (authHeader == null || authHeader.isBlank()) {
            log.debug("No Authorization header provided");
            return null;
        }
        
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        Long userId = jwtUtil.extractUserId(token);
        
        if (userId != null && userId != -1L) {
            log.info("UserId extracted successfully");
            return userId;
        }
        
        log.warn("Failed to extract userId from Authorization header");
        return null;
    }

    /**
     * Get all passwords for a specific user, decrypting them on retrieval.
     */
    private List<Password> getPasswordsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            log.warn("Invalid userId: {}", userId);
            return Collections.emptyList();
        }
        
        List<Password> passwords = passwordRepository.findByUserId(userId);
        
        // Defend against null
        if (passwords == null || passwords.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Decrypt all sensitive fields before returning
        passwords.forEach(this::decryptPasswordFields);
        
        return passwords;
    }

    /**
     * Decrypt all sensitive fields of a password entity.
     * If decryption fails, keeps the encrypted value.
     */
    private void decryptPasswordFields(Password p) {
        // Decrypt password field
        if (p.getPassword() != null) {
            try {
                String decrypted = encryption.decrypt(p.getPassword());
                if (decrypted != null) {
                    p.setPassword(decrypted);
                } else {
                    log.warn("Decryption returned null for password field of id {}, keeping encrypted", p.getId());
                }
            } catch (Exception e) {
                log.error("Failed to decrypt password field for id {}: {}", p.getId(), e.getMessage());
                // Keep encrypted value if decryption fails
            }
        }
        
        // Decrypt website field
        if (p.getWebsite() != null) {
            try {
                String decrypted = encryption.decrypt(p.getWebsite());
                if (decrypted != null) {
                    p.setWebsite(decrypted);
                } else {
                    log.warn("Decryption returned null for website field of id {}, keeping encrypted", p.getId());
                }
            } catch (Exception e) {
                log.error("Failed to decrypt website field for id {}: {}", p.getId(), e.getMessage());
                // Keep encrypted value if decryption fails
            }
        }
        
        // Decrypt name/title field
        if (p.getName() != null) {
            try {
                String decrypted = encryption.decrypt(p.getName());
                if (decrypted != null) {
                    p.setName(decrypted);
                } else {
                    log.warn("Decryption returned null for name/title field of id {}, keeping encrypted", p.getId());
                }
            } catch (Exception e) {
                log.error("Failed to decrypt name/title field for id {}: {}", p.getId(), e.getMessage());
                // Keep encrypted value if decryption fails
            }
        }
    }

    public Password updatePassword(Password password) {
        // Encrypt all sensitive fields before saving
        if (password.getPassword() != null && !password.getPassword().isEmpty()) {
            password.setPassword(encryption.encrypt(password.getPassword()));
        }
        if (password.getWebsite() != null && !password.getWebsite().isEmpty()) {
            password.setWebsite(encryption.encrypt(password.getWebsite()));
        }
        if (password.getName() != null && !password.getName().isEmpty()) {
            password.setName(encryption.encrypt(password.getName()));
        }
        
        Password saved = passwordRepository.save(password);

        // Decrypt fields for the returned object
        decryptPasswordFields(saved);
        return saved;
    }
}
