package com.sofka.cotizador.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LayoutUbicacionesRequest(
        @Min(1) @Max(20) Integer numeroUbicaciones,
        @NotNull @Valid SeccionesAplicanRequest seccionesAplican
) {
    public record SeccionesAplicanRequest(
            boolean direccion,
            boolean datosTecnicos,
            boolean giroComercial,
            boolean garantias
    ) {}
}
