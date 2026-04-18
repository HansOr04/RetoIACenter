package com.sofka.cotizador.infrastructure.http;

import com.sofka.cotizador.domain.exception.CoreServiceUnavailableException;
import com.sofka.cotizador.domain.port.CoreFolioService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;

// HU-001 — llama al core-stub para confirmar disponibilidad y genera el número de folio
@Component
public class CoreFolioClient implements CoreFolioService {

    private static final Logger log = LoggerFactory.getLogger(CoreFolioClient.class);

    private final RestClient restClient;

    public CoreFolioClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    @CircuitBreaker(name = "core-stub", fallbackMethod = "fallback")
    public String generarNumeroFolio(String codigoAgente) {
        // Verifica que el core-stub está disponible (health check)
        restClient.get()
                .uri("/health")
                .retrieve()
                .toBodilessEntity();

        int anio = Year.now().getValue();
        int secuencia = ThreadLocalRandom.current().nextInt(1, 10000);
        String numeroFolio = String.format("F%d-%04d", anio, secuencia);

        log.info("Folio generado: {} (agente={})", numeroFolio, codigoAgente);
        return numeroFolio;
    }

    public String fallback(String codigoAgente, Throwable cause) {
        log.error("Core-stub no disponible al generar folio para agente={}: {}", codigoAgente, cause.getMessage());
        throw new CoreServiceUnavailableException("El sistema core no está disponible", cause);
    }
}
