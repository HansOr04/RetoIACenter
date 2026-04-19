package com.sofka.cotizador.domain.model.calculo;

import java.math.BigDecimal;

public record ParametrosCalculo(
        BigDecimal factorComercial,
        BigDecimal tasaExtension,
        BigDecimal tasaRemocion,
        BigDecimal tasaGastosExt,
        BigDecimal tasaPerdidaRentas,
        BigDecimal tasaBi,
        BigDecimal tasaDinero,
        BigDecimal tasaVidrios,
        BigDecimal tasaAnuncios
) {}
