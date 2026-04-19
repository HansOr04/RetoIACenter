package com.sofka.cotizador.interfaces.rest.dto;

import java.math.BigDecimal;
import java.util.List;

public record CalculoResponse(
        BigDecimal primaNeta,
        BigDecimal primaComercial,
        BigDecimal factorComercial,
        List<PrimaPorUbicacionData> primasPorUbicacion,
        String fechaCalculo,
        String estado,
        int version
) {}
