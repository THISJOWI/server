package uk.thisjowi.OTP.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import uk.thisjowi.OTP.dto.UserRegisteredEvent;
import uk.thisjowi.OTP.service.OtpService;

/**
 * Service for consuming Kafka messages from Authentication service
 */
@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final ObjectMapper objectMapper;
    private final OtpService otpService;

    @Autowired
    public KafkaConsumerService(ObjectMapper objectMapper, OtpService otpService) {
        this.objectMapper = objectMapper;
        this.otpService = otpService;
    }

    /**
     * Listen for user registration events from Authentication service
     * When a user registers, automatically create an OTP for them
     */
    @KafkaListener(topics = "${kafka.topic.auth-events:auth-events}", groupId = "${kafka.consumer.group-id:otp-service-group}")
    public void handleUserRegisteredEvent(String message) {
        try {
            logger.info("Received message from Kafka: {}", message);
            
            UserRegisteredEvent event = objectMapper.readValue(message, UserRegisteredEvent.class);
            
            if ("USER_REGISTERED".equals(event.getEventType())) {
                logger.info("Processing user registration event for user: {}", event.getEmail());
                
                // Automatically create an OTP for the newly registered user
                // Valid for 30 days (2592000 seconds)
                otpService.createOtpForUser(event.getUserId(), event.getEmail(), "TOTP", 2592000L);
                
                logger.info("OTP automatically created for user: {}", event.getEmail());
            }
            
        } catch (Exception e) {
            logger.error("Error processing user registered event", e);
        }
    }

    /**
     * Generic listener for OTP-related events (if needed for inter-service communication)
     */
    @KafkaListener(topics = "${kafka.topic.otp-events:otp-events}", groupId = "${kafka.consumer.group-id:otp-service-group}")
    public void handleOtpEvents(String message) {
        logger.info("Received OTP event from Kafka: {}", message);
        // Process OTP events if needed
    }
}
