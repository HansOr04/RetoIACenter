package com.sofka.cotizador.interfaces.rest.dto;

import com.sofka.cotizador.domain.model.ubicacion.AlertaBloqueante;

import java.util.List;

public record ResumenUbicacionesResponse(
        int total,
        int completas,
        int incompletas,
        int calculables,
        List<Integer> indicesIncompletos,
        List<DetalleIncompleta> detalleIncompletas
) {
    public record DetalleIncompleta(
            int indice,
            String nombreUbicacion,
            List<AlertaBloqueante> alertas
    ) {}
}
