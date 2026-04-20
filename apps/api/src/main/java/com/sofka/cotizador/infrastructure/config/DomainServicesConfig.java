package com.sofka.cotizador.infrastructure.config;

import com.sofka.cotizador.domain.service.CalculoPrimaService;
import com.sofka.cotizador.domain.service.ValidadorUbicacion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra los servicios de dominio como beans de Spring
 * sin acoplar el dominio al framework.
 *
 * El dominio permanece puro (sin anotaciones de Spring),
 * y la infraestructura es quien decide cómo instanciarlos.
 */
@Configuration
public class DomainServicesConfig {

    @Bean
    public ValidadorUbicacion validadorUbicacion() {
        return new ValidadorUbicacion();
    }

    @Bean
    public CalculoPrimaService calculoPrimaService() {
        return new CalculoPrimaService();
    }
}
