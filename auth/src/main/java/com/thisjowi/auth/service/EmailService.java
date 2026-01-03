package com.thisjowi.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${mailtrap.api.token}")
    private String mailtrapApiToken;

    @Value("${mailtrap.sender.email}")
    private String senderEmail;

    @Value("${mailtrap.sender.name}")
    private String senderName;

    private final RestTemplate restTemplate;

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendVerificationEmail(String to, String verificationToken) {
        String url = "https://send.api.mailtrap.io/api/send";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(mailtrapApiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        
        Map<String, String> from = new HashMap<>();
        from.put("email", senderEmail);
        from.put("name", senderName);
        body.put("from", from);

        Map<String, String> recipient = new HashMap<>();
        recipient.put("email", to);
        body.put("to", Collections.singletonList(recipient));

        body.put("subject", "Verify your email");
        body.put("text", "Please verify your email using this token: " + verificationToken);
        body.put("html", "<p>Please verify your email using this token: <strong>" + verificationToken + "</strong></p>");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, request, String.class);
    }
}
