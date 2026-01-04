package com.thisjowi.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import jakarta.annotation.PostConstruct;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${mailtrap.api.token}")
    private String mailtrapApiToken;

    @Value("${mailtrap.sender.email}")
    private String senderEmail;

    @Value("${mailtrap.sender.name}")
    private String senderName;

    @Value("${mailtrap.api.url:https://send.api.mailtrap.io/api/send}")
    private String mailtrapApiUrl;

    private final RestTemplate restTemplate;

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        if (mailtrapApiUrl != null) {
            mailtrapApiUrl = mailtrapApiUrl.trim();
            if (mailtrapApiUrl.startsWith("//")) {
                mailtrapApiUrl = "https:" + mailtrapApiUrl;
            }
        }
    }

    public void sendVerificationEmail(String to, String verificationToken) {
        log.info("Preparing to send verification email to: {} using URL: {}", to, mailtrapApiUrl);

        if (mailtrapApiUrl == null || !mailtrapApiUrl.startsWith("http")) {
            log.error("Invalid Mailtrap API URL: {}", mailtrapApiUrl);
            throw new IllegalArgumentException("Invalid Mailtrap API URL: " + mailtrapApiUrl);
        }
        
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
        body.put("html", getVerificationEmailTemplate(verificationToken));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(mailtrapApiUrl, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Verification email sent successfully to {}", to);
            } else {
                log.error("Failed to send email. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("Error occurred while sending email to {}: {}", to, e.getMessage());
            throw e;
        }
    }

    private String getVerificationEmailTemplate(String token) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/verification-email.html");
            String template = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return template.replace("{{verificationToken}}", token);
        } catch (IOException e) {
            log.error("Error loading email template", e);
            // Fallback to simple HTML if template loading fails
            return "<p>Please verify your email using this token: <strong>" + token + "</strong></p>";
        }
    }
}
