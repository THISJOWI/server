package com.thisjowi.otp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.thisjowi.otp.dto.OtpCreatedEvent;
import com.thisjowi.otp.entity.otp;
import com.thisjowi.otp.kafka.KafkaProducerService;
import com.thisjowi.otp.repository.OtpRepository;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class OtpService {
    
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    
    @Autowired
    private OtpRepository otpRepository;
    
    @Autowired
    private KafkaProducerService kafkaProducerService;

    public List<otp> getAllOtps(Long userId) {
        if (userId != null) {
            return otpRepository.findByUserId(userId);
        }
        return List.of();
    }

    public Optional<otp> getOtp(Long id) {
        return otpRepository.findById(id);
    }

    public otp createOtp(Long userId, String name, String type, String secret, String issuer, Integer digits, Integer period, String algorithm) {
        // Check for duplicates if secret is provided
        if (secret != null && !secret.isEmpty()) {
            String normalizedSecret = secret.trim().replace(" ", "").toUpperCase();
            List<otp> existing = otpRepository.findByUserId(userId);
            for (otp entry : existing) {
                String existingSecret = entry.getSecret();
                if (existingSecret != null) {
                    String normalizedExisting = existingSecret.trim().replace(" ", "").toUpperCase();
                    if (normalizedExisting.equals(normalizedSecret)) {
                        logger.info("Duplicate OTP creation attempt prevented for user {} with secret ending in ...{}", userId, normalizedSecret.substring(Math.max(0, normalizedSecret.length() - 4)));
                        return entry; // Return existing entry
                    }
                }
            }
        }

        otp o = new otp();
        o.setUserId(userId);
        o.setEmail(name);
        o.setType(type);
        o.setSecret((secret != null && !secret.isEmpty()) ? secret : generateSecret());
        o.setIssuer(issuer);
        o.setDigits(digits);
        o.setPeriod(period);
        o.setAlgorithm(algorithm);
        o.setValid(true);
        o.setExpiresAt(System.currentTimeMillis() + (period != null ? period * 1000L : 30000L)); // Default 30s if null

        otp saved = otpRepository.save(o);
        
        // Send event to Kafka
        OtpCreatedEvent event = new OtpCreatedEvent(
            saved.getId(), 
            userId, 
            name, 
            type, 
            "OTP_CREATED", 
            System.currentTimeMillis(), 
            saved.getExpiresAt()
        );
        kafkaProducerService.sendOtpCreatedEvent(event);
        
        return saved;
    }
    
    /**
     * Create OTP for a specific user with userId
     */
    public otp createOtpForUser(Long userId, String email, String type, long validitySeconds) {
        otp o = new otp();
        o.setUserId(userId);
        o.setEmail(email);
        o.setType(type);
        o.setValid(true);
        o.setExpiresAt(System.currentTimeMillis() + (validitySeconds * 1000));
        o.setSecret(generateSecret());
        otp saved = otpRepository.save(o);
        
        // Send Kafka event with userId when OTP is created
        try {
            OtpCreatedEvent event = new OtpCreatedEvent(
                saved.getId(),
                userId,
                email,
                type,
                "OTP_CREATED",
                System.currentTimeMillis(),
                saved.getExpiresAt()
            );
            kafkaProducerService.sendOtpCreatedEvent(event);
            logger.info("OTP created for user: {} with userId: {}", email, userId);
        } catch (Exception e) {
            logger.error("Error sending OTP created event to Kafka", e);
        }
        
        return saved;
    }

    public otp updateOtp(Long id, otp updatedOtp) {
        updatedOtp.setId(id);
        return otpRepository.save(updatedOtp);
    }

    public void deleteOtp(Long id) {
        otpRepository.deleteById(id);
    }

    public boolean validateOtp(Long id, String code) {
        Optional<otp> o = otpRepository.findById(id);
        if (o.isPresent() && o.get().getValid() && o.get().getExpiresAt() > System.currentTimeMillis()) {
            // Aquí deberías implementar la validación TOTP/HOTP real
            return o.get().getSecret().equals(code);
        }
        return false;
    }

    private String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
