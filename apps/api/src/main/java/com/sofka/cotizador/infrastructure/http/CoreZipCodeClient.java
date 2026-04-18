package com.sofka.cotizador.infrastructure.http;

import com.sofka.cotizador.domain.model.ubicacion.ZonaCatastrofica;
import com.sofka.cotizador.domain.port.ValidadorCodigoPostalService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Component
public class CoreZipCodeClient implements ValidadorCodigoPostalService {

    private static final Logger log = LoggerFactory.getLogger(CoreZipCodeClient.class);

    private final RestClient restClient;

    public CoreZipCodeClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    @CircuitBreaker(name = "core-stub", fallbackMethod = "fallbackValidar")
    public Optional<ZonaCatastrofica> validarCodigoPostal(String codigoPostal) {
        log.info("Validando código postal cp={}", codigoPostal);
        try {
            ZipCodeResponse response = restClient.get()
                    .uri("/v1/zip-codes/validate?cp={cp}", codigoPostal)
                    .retrieve()
                    .body(ZipCodeResponse.class);

            if (response == null) {
                log.warn("Respuesta vacía al validar cp={}", codigoPostal);
                return Optional.empty();
            }

            return Optional.of(new ZonaCatastrofica(response.zonaTev(), response.zonaFhm()));
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Código postal no encontrado cp={}", codigoPostal);
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Error al validar código postal cp={}: {}", codigoPostal, e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<ZonaCatastrofica> fallbackValidar(String codigoPostal, Throwable cause) {
        log.warn("Fallback activado para validación de cp={}: {}", codigoPostal, cause.getMessage());
        return Optional.empty();
    }

    private record ZipCodeResponse(String zonaTev, String zonaFhm) {}
}
