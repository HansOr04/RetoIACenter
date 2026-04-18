package com.sofka.cotizador.interfaces.rest.dto;

public record DatosGeneralesResponse(
        String numeroFolio,
        String estadoCotizacion,
        Integer version,
        String fechaUltimaActualizacion,
        DatosGeneralesData datosGenerales
) {}
