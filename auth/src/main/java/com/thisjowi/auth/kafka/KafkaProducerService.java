package com.thisjowi.auth.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.thisjowi.auth.dto.UserRegisteredEvent;
import com.thisjowi.auth.entity.User;

import java.time.Instant;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
        logger.info("Message sent to Kafka topic {}: {}", topic, message);
    }
    
    /**
     * Send user registered event to Kafka
     */
    public void sendUserRegisteredEvent(User user) {
        try {
            UserRegisteredEvent event = new UserRegisteredEvent(
                user.getId(),
                user.getEmail(),
                "USER_REGISTERED",
                Instant.now().getEpochSecond()
            );
            
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("auth-events", user.getId().toString(), message);
            logger.info("User registered event sent to Kafka: userId={}, email={}", 
                user.getId(), user.getEmail());
        } catch (JsonProcessingException e) {
            logger.error("Error serializing user registered event", e);
        }
    }
}
