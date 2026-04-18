package com.sofka.cotizador.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class IfMatchInterceptorTest {

    private IfMatchHeaderInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new IfMatchHeaderInterceptor();
    }

    @Test
    void debeRechazarPUTSinIfMatch() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/api/v1/quotes/F2026-0001/locations");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean resultado = interceptor.preHandle(request, response, new Object());

        assertThat(resultado).isFalse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.PRECONDITION_REQUIRED.value());
        assertThat(response.getContentType()).contains("application/problem+json");
        assertThat(response.getContentAsString()).contains("If-Match");
    }

    @Test
    void debePermitirPUTConIfMatchPresente() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/api/v1/quotes/F2026-0001/locations");
        request.addHeader("If-Match", "1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean resultado = interceptor.preHandle(request, response, new Object());

        assertThat(resultado).isTrue();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void debePermitirGETSinIfMatch() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/quotes/F2026-0001/locations");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean resultado = interceptor.preHandle(request, response, new Object());

        assertThat(resultado).isTrue();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
