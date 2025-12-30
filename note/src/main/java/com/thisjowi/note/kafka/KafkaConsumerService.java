package com.thisjowi.note.kafka;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    // Stores the latest token received
    public static final AtomicReference<String> LAST_TOKEN = new AtomicReference<>(null);

    @KafkaListener(topics = "auth-events", groupId = "notes-service-group")
    public void listen(String message) {
        // We assume that the message is the JWT token
        System.out.println("[Notes] Token received from Kafka: " + message);
        LAST_TOKEN.set(message);
    }
}
