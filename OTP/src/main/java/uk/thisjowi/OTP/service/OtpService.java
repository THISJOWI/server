package uk.thisjowi.OTP.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.thisjowi.OTP.dto.OtpCreatedEvent;
import uk.thisjowi.OTP.entity.otp;
import uk.thisjowi.OTP.kafka.KafkaProducerService;
import uk.thisjowi.OTP.repository.OtpRepository;

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

    public List<otp> getAllOtps() {
        return otpRepository.findAll();
    }

    public Optional<otp> getOtp(Long id) {
        return otpRepository.findById(id);
    }

    public otp createOtp(String user, String type, long validitySeconds) {
        otp o = new otp();
        o.setUsername(user);
        o.setType(type);
        o.setValid(true);
        o.setExpiresAt(Instant.now().getEpochSecond() + validitySeconds);
        o.setSecret(generateSecret());
        otp saved = otpRepository.save(o);
        
        // Send Kafka event when OTP is created
        try {
            OtpCreatedEvent event = new OtpCreatedEvent(
                saved.getId(),
                null, // userId will be set if available
                user,
                type,
                "OTP_CREATED",
                Instant.now().getEpochSecond(),
                saved.getExpiresAt()
            );
            kafkaProducerService.sendOtpCreatedEvent(event);
        } catch (Exception e) {
            logger.error("Error sending OTP created event to Kafka", e);
        }
        
        return saved;
    }
    
    /**
     * Create OTP for a specific user with userId
     */
    public otp createOtpForUser(Long userId, String username, String type, long validitySeconds) {
        otp o = new otp();
        o.setUserId(userId);
        o.setUsername(username);
        o.setType(type);
        o.setValid(true);
        o.setExpiresAt(Instant.now().getEpochSecond() + validitySeconds);
        o.setSecret(generateSecret());
        otp saved = otpRepository.save(o);
        
        // Send Kafka event with userId when OTP is created
        try {
            OtpCreatedEvent event = new OtpCreatedEvent(
                saved.getId(),
                userId,
                username,
                type,
                "OTP_CREATED",
                Instant.now().getEpochSecond(),
                saved.getExpiresAt()
            );
            kafkaProducerService.sendOtpCreatedEvent(event);
            logger.info("OTP created for user: {} with userId: {}", username, userId);
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
        if (o.isPresent() && o.get().getValid() && o.get().getExpiresAt() > Instant.now().getEpochSecond()) {
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
