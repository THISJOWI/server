package uk.thisjowi.Password.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Allow all requests to /api/v1/passwords (authentication is done via JWT token in controller)
                        .requestMatchers("/api/v1/passwords", "/api/v1/passwords/**").permitAll()
                        // Allow Eureka health checks
                        .requestMatchers("/actuator/**", "/health", "/info").permitAll()
                        // Deny all other requests
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir orígenes para desarrollo web y móvil (iPhone, Android)
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:*",           // Desarrollo web
            "http://127.0.0.1:*",            // Desarrollo web alternativo
            "capacitor://localhost",         // Capacitor iOS/Android
            "ionic://localhost",             // Ionic iOS/Android
            "http://192.168.*.*:*",          // Red local WiFi
            "http://10.*.*.*:*",             // Red local privada
            "http://172.16.*.*:*",           // Red local privada
            "https://*"                      // HTTPS en producción
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
