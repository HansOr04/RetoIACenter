package com.sofka.cotizador.interfaces.rest.dto;

import java.util.List;
import java.util.Map;

public record EstadoFolioResponse(
        String numeroFolio,
        String estadoCotizacion,
        Integer version,
        String fechaCreacion,
        String fechaUltimaActualizacion,
        int porcentajeProgreso,
        boolean esCalculable,
        List<AlertaData> alertas,
        Map<String, Boolean> seccionesCompletadas
) {
    public record AlertaData(
            String seccion,
            String mensaje
    ) {}
}
