package uk.thisjowi.Authentication.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.thisjowi.Authentication.entity.User;
import uk.thisjowi.Authentication.repository.UserRepository;
import uk.thisjowi.Authentication.kafka.KafkaProducerService;
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
            
            log.info("Found user with username: {} for deletion", user.getUsername());
            
            // Delete the user
            userRepository.delete(user);
            userRepository.flush();  // Ensure the delete is immediately persisted
            
            log.info("Successfully deleted user with ID: {}", userId);
            
            // Programmatically evict usersByUsername cache entry if present
            if (user.getUsername() != null) {
                var cache = cacheManager.getCache("usersByUsername");
                if (cache != null) {
                    cache.evict(user.getUsername());
                    log.debug("Evicted usersByUsername cache for: {}", user.getUsername());
                }
            }
            
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

    /**
     * Delete a user by username and evict related caches (by username and id).
     */
    @Transactional
    @CacheEvict(cacheNames = {"usersByUsername"}, key = "#username")
    public void deleteUserByUsername(String username) {
        try {
            log.info("Attempting to delete user: {}", username);
            
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.warn("User not found for deletion: {}", username);
                        return new UsernameNotFoundException("User not found: " + username);
                    });
            
            log.info("Found user with ID: {} for deletion", user.getId());
            
            // Delete the user
            userRepository.delete(user);
            userRepository.flush();  // Ensure the delete is immediately persisted
            
            log.info("Successfully deleted user: {}", username);
            
            // Programmatically evict usersById cache entry if present
            if (user.getId() != null) {
                var cache = cacheManager.getCache("usersById");
                if (cache != null) {
                    cache.evict(user.getId());
                    log.debug("Evicted usersById cache for ID: {}", user.getId());
                }
            }
        } catch (UsernameNotFoundException e) {
            log.error("User not found during deletion: {}", username);
            throw e;
        } catch (Exception e) {
            log.error("Error deleting user {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    @Cacheable(cacheNames = "usersByUsername", key = "#username")
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Cacheable(cacheNames = "usersByEmail", key = "#email")
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Save (create or update) a user and update caches for id and username.
     * If user has an ID, it will be updated. Otherwise, it will be created.
     */
    @Transactional
    @CachePut(cacheNames = "usersByUsername", key = "#result.username")
    public User saveUser(User user) {
        try {
            boolean isNewUser = (user.getId() == null);
            
            // If user has an ID, make sure we merge it to attach to the session
            if (user.getId() != null) {
                log.debug("Updating existing user with ID: {}", user.getId());
                user = userRepository.save(user);  // This should trigger an UPDATE
            } else {
                log.debug("Creating new user: {}", user.getUsername());
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
                log.info("User registered event sent to Kafka for user: {} (ID: {})", user.getUsername(), user.getId());
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
            if (user.getUsername() != null) {
                var usernameCache = cacheManager.getCache("usersByUsername");
                if (usernameCache != null) {
                    usernameCache.evict(user.getUsername());
                }
            }
            
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
}