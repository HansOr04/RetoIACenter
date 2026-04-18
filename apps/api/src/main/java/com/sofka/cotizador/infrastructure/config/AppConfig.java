package com.sofka.cotizador.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

// HU-001 — configuración de beans de infraestructura
@Configuration
public class AppConfig {

    @Bean
    public RestClient restClient(@Value("${core.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
