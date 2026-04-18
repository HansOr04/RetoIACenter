package com.sofka.cotizador.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UbicacionRequest(
        @NotBlank String nombreUbicacion,
        @NotBlank String direccion,
        @NotBlank String codigoPostal,
        String estado,
        String municipio,
        String colonia,
        String ciudad,
        @NotBlank String tipoConstructivo,
        Integer nivel,
        Integer anioConstruccion,
        @NotNull GiroRequest giro,
        List<String> garantias
) {}
