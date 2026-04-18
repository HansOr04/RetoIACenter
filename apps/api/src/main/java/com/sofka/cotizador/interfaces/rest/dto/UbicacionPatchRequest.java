package com.sofka.cotizador.interfaces.rest.dto;

import java.util.List;

public record UbicacionPatchRequest(
        String nombreUbicacion,
        String direccion,
        String codigoPostal,
        String tipoConstructivo,
        Integer nivel,
        Integer anioConstruccion,
        GiroRequest giro,
        List<String> garantias
) {}
