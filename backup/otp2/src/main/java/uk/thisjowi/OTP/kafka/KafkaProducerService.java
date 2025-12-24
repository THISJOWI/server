package uk.thisjowi.OTP.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uk.thisjowi.OTP.dto.OtpCreatedEvent;

/**
 * Service for producing Kafka messages to notify other services about OTP events
 */
@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.otp-events:otp-events}")
    private String otpEventsTopic;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Send OTP created event to Kafka
     */
    public void sendOtpCreatedEvent(OtpCreatedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(otpEventsTopic, event.getUserId().toString(), message);
            logger.info("OTP created event sent to Kafka: userId={}, otpId={}", 
                event.getUserId(), event.getOtpId());
        } catch (JsonProcessingException e) {
            logger.error("Error serializing OTP created event", e);
        }
    }

    /**
     * Send a generic message to a specific topic
     */
    public void sendMessage(String topic, String key, String message) {
        kafkaTemplate.send(topic, key, message);
        logger.info("Message sent to Kafka topic {}: {}", topic, message);
    }
}
