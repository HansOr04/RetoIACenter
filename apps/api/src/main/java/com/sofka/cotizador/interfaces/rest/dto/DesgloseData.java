package com.sofka.cotizador.interfaces.rest.dto;

import java.math.BigDecimal;

public record DesgloseData(
        BigDecimal incendioEdificios,
        BigDecimal incendioContenidos,
        BigDecimal extensionCobertura,
        BigDecimal catTev,
        BigDecimal catFhm,
        BigDecimal remocionEscombros,
        BigDecimal gastosExtraordinarios,
        BigDecimal perdidaRentas,
        BigDecimal bi,
        BigDecimal equipoElectronico,
        BigDecimal robo,
        BigDecimal dineroValores,
        BigDecimal vidrios,
        BigDecimal anunciosLuminosos,
        BigDecimal total
) {}
