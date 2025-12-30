package com.thisjowi.api.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración centralizada de OpenAPI para Spring Cloud Gateway
 * Agrega las definiciones de todos los microservicios conectados al gateway
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8100}")
    private String serverPort;

    @Bean
    public OpenAPI gatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gateway - Microservices Documentation")
                        .description("Documentación centralizada de todos los microservicios expuestos a través del API Gateway")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("API Gateway")
                                .url("http://localhost:" + serverPort)))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Gateway Server"),
                        new Server()
                                .url("http://api-gateway:8100")
                                .description("Docker Gateway Server")
                ));
    }
}
