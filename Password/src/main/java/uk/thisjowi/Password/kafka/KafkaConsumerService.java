package uk.thisjowi.Password.kafka;

import java.util.concurrent.atomic.AtomicReference;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kafka consumer to receive authentication events from auth-service.
 * Stores the latest JWT token for use in password operations.
 */
@Service
public class KafkaConsumerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);
    
    // Stores the latest JWT token received from auth-service
    public static final AtomicReference<String> LAST_TOKEN = new AtomicReference<>(null);

    @KafkaListener(topics = "auth-events", groupId = "password-service-group")
    public void listen(String message) {
        log.debug("[Password] Received message from Kafka topic 'auth-events': {}", message);
        if (message != null && message.startsWith("Bearer ")) {
            LAST_TOKEN.set(message);
            log.debug("[Password] Token updated from Kafka");
        } else if (message != null && message.length() > 10) {
            // Assume it's a JWT token
            LAST_TOKEN.set(message);
            log.debug("[Password] JWT token updated from Kafka");
        }
    }
}

