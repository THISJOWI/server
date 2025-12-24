package com.thisjowi.auth.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.cache.CacheManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thisjowi.auth.entity.Account;
import com.thisjowi.auth.entity.Deployment;
import com.thisjowi.auth.entity.User;
import com.thisjowi.auth.repository.UserRepository;
import com.thisjowi.auth.kafka.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    private final KafkaProducerService kafkaProducerService;

    public UserService(UserRepository userRepository, CacheManager cacheManager, KafkaProducerService kafkaProducerService) {
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Delete a user by ID and evict related caches.
     * Wrapped in transaction to ensure atomic delete operation.
     */
    @Transactional
    public void deleteUserById(Long userId) {
        try {
            log.info("Attempting to delete user with ID: {}", userId);
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("User not found for deletion with ID: {}", userId);
                        return new UsernameNotFoundException("User not found with ID: " + userId);
                    });
            
            log.info("Found user with email: {} for deletion", user.getEmail());
            
            // Delete the user
            userRepository.delete(user);
            userRepository.flush();  // Ensure the delete is immediately persisted
            
            log.info("Successfully deleted user with ID: {}", userId);
            
            // Programmatically evict usersById cache entry if present
            var cacheById = cacheManager.getCache("usersById");
            if (cacheById != null) {
                cacheById.evict(userId);
                log.debug("Evicted usersById cache for ID: {}", userId);
            }
        } catch (UsernameNotFoundException e) {
            log.error("User not found during deletion: {}", userId);
            throw e;
        } catch (Exception e) {
            log.error("Error deleting user with ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    @Cacheable(cacheNames = "usersByEmail", key = "#email")
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Save (create or update) a user and update caches for id.
     * If user has an ID, it will be updated. Otherwise, it will be created.
     */
    @Transactional
    public User saveUser(User user) {
        try {
            boolean isNewUser = (user.getId() == null);
            
            // If user has an ID, make sure we merge it to attach to the session
            if (user.getId() != null) {
                log.debug("Updating existing user with ID: {}", user.getId());
                user = userRepository.save(user);  // This should trigger an UPDATE
            } else {
                log.debug("Creating new user: {}", user.getEmail());
                user = userRepository.save(user);  // This will trigger an INSERT
            }
            
            // Also update usersById cache
            if (user.getId() != null) {
                var cache = cacheManager.getCache("usersById");
                if (cache != null) {
                    cache.put(user.getId(), user);
                }
            }
            
            // Evict usersByEmail cache to ensure consistency
            var emailCache = cacheManager.getCache("usersByEmail");
            if (emailCache != null && user.getEmail() != null) {
                emailCache.evict(user.getEmail());
            }
            
            // Send event to Kafka when a new user is registered
            if (isNewUser) {
                kafkaProducerService.sendUserRegisteredEvent(user);
                log.info("User registered event sent to Kafka for user: {} (ID: {})", user.getEmail(), user.getId());
            } else {
                kafkaProducerService.sendMessage("auth-events", "User updated: ID=" + user.getId());
            }
            
            return user;
        } catch (Exception e) {
            log.error("Error saving user ID {}: {}", user.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
        }
    }

    @Cacheable(cacheNames = "usersById", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Update user password by user ID. This is specifically for password changes.
     * Directly queries and updates in the same transaction to avoid detached entity issues.
     */
    @Transactional
    public void updateUserPassword(Long userId, String newEncodedPassword) {
        try {
            log.info("Updating password for user ID: {}", userId);
            
            // Get fresh user from database
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("User not found for password update: {}", userId);
                        return new UsernameNotFoundException("User not found with ID: " + userId);
                    });
            
            // Update password
            user.setPassword(newEncodedPassword);
            
            // Save in same transaction
            userRepository.save(user);
            userRepository.flush();
            
            log.info("Password successfully updated for user ID: {}", userId);
            
            // Evict caches
            var userByIdCache = cacheManager.getCache("usersById");
            if (userByIdCache != null) {
                userByIdCache.evict(userId);
            }
        } catch (UsernameNotFoundException e) {
            log.error("User not found during password update: {}", userId);
            throw e;
        } catch (Exception e) {
            log.error("Error updating password for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to update password: " + e.getMessage(), e);
        }
    }

    // OPTOIONAL: Additional user information for dashboard
    // Get and set country
    public String getUserCountry(Long userId) {
        User user = getUserById(userId);
        return user.getCountry();
    }

    public void setUserCountry(Long userId, String country) {
        User user = getUserById(userId);
        user.setCountry(country);
        saveUser(user);
    }

    // Get and set birthdate
    public LocalDate getUserBirthdate(Long userId) {
        User user = getUserById(userId);
        return user.getBirthdate();
    }

    public void setUserBirthdate(Long userId, LocalDate birthdate) {
        User user = getUserById(userId);
        user.setBirthdate(birthdate);
        saveUser(user);
    }

    // Deployment type
    public Deployment getDeploymentType(Long userId) {
        User user = getUserById(userId);
        return user.getDeploymentType();
    }

    public void setDeploymentType(Long userId, Deployment deploymentType) {
        User user = getUserById(userId);
        user.setDeploymentType(deploymentType);
        saveUser(user);
    }

    // Account type
    public Account getAccountType(Long userId) {
        User user = getUserById(userId);
        return user.getAccountType();
    }

    public void setAccountType(Long userId, Account accountType) {
        User user = getUserById(userId);
        user.setAccountType(accountType);
        saveUser(user);
    }

    // Account creation date
    public LocalDateTime getAccountCreationDate(Long userId) {
        User user = getUserById(userId);
        return user.getCreatedAt();
    }




}