package com.sofka.cotizador.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record GiroRequest(
        @NotBlank String codigo,
        String descripcion,
        String claveIncendio
) {}
