package com.sofka.cotizador.interfaces.rest;

import com.sofka.cotizador.domain.exception.CoreServiceUnavailableException;
import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.exception.LayoutCapacityExceededException;
import com.sofka.cotizador.domain.exception.UbicacionNotFoundException;
import com.sofka.cotizador.domain.exception.VersionConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.stream.Collectors;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setType(URI.create("https://cotizador.sofka.com/errors/validacion"));
        problem.setTitle("Error de validación");
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setType(URI.create("https://cotizador.sofka.com/errors/regla-negocio"));
        problem.setTitle("Regla de negocio violada");
        return problem;
    }

    @ExceptionHandler(LayoutCapacityExceededException.class)
    public ProblemDetail handleCapacityExceeded(LayoutCapacityExceededException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setType(URI.create("https://cotizador.sofka.com/errors/capacity-exceeded"));
        problem.setTitle("Capacidad de ubicaciones excedida");
        return problem;
    }

    @ExceptionHandler(UbicacionNotFoundException.class)
    public ProblemDetail handleUbicacionNotFound(UbicacionNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://cotizador.sofka.com/errors/ubicacion-not-found"));
        problem.setTitle("Ubicación no encontrada");
        return problem;
    }

    @ExceptionHandler(VersionConflictException.class)
    public ProblemDetail handleVersionConflict(VersionConflictException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setType(URI.create("https://cotizador.sofka.com/errors/version-conflict"));
        problem.setTitle("Conflicto de versión");
        problem.setProperty("currentVersion", ex.getCurrentVersion());
        problem.setProperty("receivedVersion", ex.getReceivedVersion());
        problem.setProperty("numeroFolio", ex.getNumeroFolio());
        return problem;
    }
}
