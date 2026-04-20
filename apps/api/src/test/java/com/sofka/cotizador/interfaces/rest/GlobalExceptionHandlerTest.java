package com.sofka.cotizador.interfaces.rest;

import com.sofka.cotizador.domain.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testFolioNotFound() {
        FolioNotFoundException ex = new FolioNotFoundException("F-10");
        ProblemDetail pd = handler.handleFolioNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND.value(), pd.getStatus());
        assertEquals("Folio no encontrado", pd.getTitle());
    }

    @Test
    void testCoreUnavailable() {
        CoreServiceUnavailableException ex = new CoreServiceUnavailableException("core api");
        ProblemDetail pd = handler.handleCoreUnavailable(ex);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), pd.getStatus());
        assertEquals("Sistema core no disponible", pd.getTitle());
    }

    @Test
    void testMissingHeader() {
        MissingRequestHeaderException ex = new MissingRequestHeaderException("If-Match", null);
        ProblemDetail pd = handler.handleMissingHeader(ex);
        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.getStatus());
        assertEquals("Header requerido ausente", pd.getTitle());
    }

    @Test
    void testValidationException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult br = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(br);
        when(br.getFieldErrors()).thenReturn(List.of(new FieldError("obj", "campo", "error")));
        
        ProblemDetail pd = handler.handleValidation(ex);
        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.getStatus());
        assertEquals("Error de validación", pd.getTitle());
    }

    @Test
    void testIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("arg_err");
        ProblemDetail pd = handler.handleIllegalArgument(ex);
        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.getStatus());
        assertEquals("Regla de negocio violada", pd.getTitle());
    }

    @Test
    void testCapacityExceeded() {
        LayoutCapacityExceededException ex = new LayoutCapacityExceededException("F-10", 2);
        ProblemDetail pd = handler.handleCapacityExceeded(ex);
        assertEquals(HttpStatus.CONFLICT.value(), pd.getStatus());
        assertEquals("Capacidad de ubicaciones excedida", pd.getTitle());
    }

    @Test
    void testUbicacionNotFound() {
        UbicacionNotFoundException ex = new UbicacionNotFoundException("F-10", 1);
        ProblemDetail pd = handler.handleUbicacionNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND.value(), pd.getStatus());
        assertEquals("Ubicación no encontrada", pd.getTitle());
    }

    @Test
    void testVersionConflict() {
        VersionConflictException ex = new VersionConflictException("F-10", 1, 2);
        ProblemDetail pd = handler.handleVersionConflict(ex);
        assertEquals(HttpStatus.CONFLICT.value(), pd.getStatus());
        assertEquals("Conflicto de versión", pd.getTitle());
    }

    @Test
    void testCoberturaReglaViolada() {
        CoberturaReglaVioladaException ex = new CoberturaReglaVioladaException("rule violated");
        ProblemDetail pd = handler.handleCoberturaReglaViolada(ex);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), pd.getStatus());
        assertEquals("Regla de cobertura violada", pd.getTitle());
    }

    @Test
    void testSinUbicacionesCalculables() {
        SinUbicacionesCalculablesException ex = new SinUbicacionesCalculablesException(List.of());
        ProblemDetail pd = handler.handleSinUbicacionesCalculables(ex);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), pd.getStatus());
        assertEquals("Sin ubicaciones calculables", pd.getTitle());
    }
}
