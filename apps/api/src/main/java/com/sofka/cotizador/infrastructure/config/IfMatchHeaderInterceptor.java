package com.sofka.cotizador.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URI;

public class IfMatchHeaderInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(IfMatchHeaderInterceptor.class);
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String method = request.getMethod();
        if (!"PUT".equalsIgnoreCase(method) && !"PATCH".equalsIgnoreCase(method)) {
            return true;
        }

        String ifMatch = request.getHeader("If-Match");
        if (ifMatch != null && !ifMatch.isBlank()) {
            return true;
        }

        log.warn("Solicitud {} {} rechazada: cabecera If-Match ausente", method, request.getRequestURI());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.PRECONDITION_REQUIRED,
                "El header If-Match es requerido para operaciones de modificación"
        );
        problem.setType(URI.create("https://cotizador.sofka.com/errors/precondition-required"));
        problem.setTitle("Header If-Match requerido");

        response.setStatus(HttpStatus.PRECONDITION_REQUIRED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.getWriter().write(MAPPER.writeValueAsString(problem));
        response.getWriter().flush();

        return false;
    }
}
