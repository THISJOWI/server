package com.thisjowi.auth.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "auth-events", groupId = "auth-service-group")
    public void listen(String message) {
        // Here you can handle messages received from other microservices
        System.out.println("Received message from Kafka: " + message);
    }
}
