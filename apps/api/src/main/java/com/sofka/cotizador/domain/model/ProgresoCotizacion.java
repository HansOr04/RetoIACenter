package com.sofka.cotizador.domain.model;

import java.util.List;
import java.util.Map;

public record ProgresoCotizacion(
        int porcentajeProgreso,
        boolean esCalculable,
        List<AlertaProgreso> alertas,
        Map<String, Boolean> seccionesCompletadas
) {}
