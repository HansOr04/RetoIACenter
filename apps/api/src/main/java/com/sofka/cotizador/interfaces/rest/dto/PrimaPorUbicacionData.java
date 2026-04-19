package com.sofka.cotizador.interfaces.rest.dto;

import java.math.BigDecimal;
import java.util.List;

public record PrimaPorUbicacionData(
        int indice,
        boolean calculada,
        BigDecimal total,
        DesgloseData desglose,
        List<String> alertas
) {}
