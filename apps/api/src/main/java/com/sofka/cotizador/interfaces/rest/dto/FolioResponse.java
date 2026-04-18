package com.sofka.cotizador.interfaces.rest.dto;

import java.time.LocalDateTime;

// HU-001
public record FolioResponse(
        String numeroFolio,
        String estadoCotizacion,
        String tipoNegocio,
        String codigoAgente,
        Integer version,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaUltimaActualizacion
) {}
