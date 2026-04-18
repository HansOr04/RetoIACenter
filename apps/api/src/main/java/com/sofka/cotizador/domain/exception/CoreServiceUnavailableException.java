package com.sofka.cotizador.domain.exception;

// HU-001 — lanzada cuando el core-stub no responde o el circuit breaker está abierto
public class CoreServiceUnavailableException extends RuntimeException {

    public CoreServiceUnavailableException(String message) {
        super(message);
    }

    public CoreServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
