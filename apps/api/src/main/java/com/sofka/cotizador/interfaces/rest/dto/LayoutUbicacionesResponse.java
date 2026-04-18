package com.sofka.cotizador.interfaces.rest.dto;

public record LayoutUbicacionesResponse(
        String numeroFolio,
        String estadoCotizacion,
        Integer version,
        String fechaUltimaActualizacion,
        LayoutUbicacionesData layoutUbicaciones
) {
    public record LayoutUbicacionesData(
            Integer numeroUbicaciones,
            SeccionesAplicanData seccionesAplican
    ) {}

    public record SeccionesAplicanData(
            boolean direccion,
            boolean datosTecnicos,
            boolean giroComercial,
            boolean garantias
    ) {}
}
