package com.sofka.cotizador.interfaces.rest;

import com.sofka.cotizador.domain.exception.CoreServiceUnavailableException;
import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

// HU-001 — manejo de errores con RFC 7807 ProblemDetail
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FolioNotFoundException.class)
    public ProblemDetail handleFolioNotFound(FolioNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://cotizador.sofka.com/errors/folio-not-found"));
        problem.setTitle("Folio no encontrado");
        return problem;
    }

    @ExceptionHandler(CoreServiceUnavailableException.class)
    public ProblemDetail handleCoreUnavailable(CoreServiceUnavailableException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        problem.setType(URI.create("https://cotizador.sofka.com/errors/core-unavailable"));
        problem.setTitle("Sistema core no disponible");
        return problem;
    }

    @ExceptionHandler(org.springframework.web.bind.MissingRequestHeaderException.class)
    public ProblemDetail handleMissingHeader(org.springframework.web.bind.MissingRequestHeaderException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Header requerido ausente: " + ex.getHeaderName());
        problem.setType(URI.create("https://cotizador.sofka.com/errors/missing-header"));
        problem.setTitle("Header requerido ausente");
        return problem;
    }
}
