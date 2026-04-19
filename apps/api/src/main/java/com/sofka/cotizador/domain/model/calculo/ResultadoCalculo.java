package com.sofka.cotizador.domain.model.calculo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ResultadoCalculo(
        BigDecimal primaNeta,
        BigDecimal primaComercial,
        BigDecimal factorComercial,
        List<PrimaPorUbicacion> primasPorUbicacion,
        LocalDateTime fechaCalculo
) {}
