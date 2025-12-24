package uk.thisjowi.Notes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @Bean
    public WebClient authenticationWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(authServiceUrl)
                .build();
    }
}