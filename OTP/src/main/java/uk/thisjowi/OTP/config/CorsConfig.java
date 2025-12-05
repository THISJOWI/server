package uk.thisjowi.OTP.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                    "http://localhost:*",           // Desarrollo web
                    "http://127.0.0.1:*",            // Desarrollo web alternativo
                    "capacitor://localhost",         // Capacitor iOS/Android
                    "ionic://localhost",             // Ionic iOS/Android
                    "http://192.168.*.*:*",          // Red local WiFi
                    "http://10.*.*.*:*",             // Red local privada
                    "http://172.16.*.*:*",           // Red local privada
                    "https://*"                      // HTTPS en producción
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
