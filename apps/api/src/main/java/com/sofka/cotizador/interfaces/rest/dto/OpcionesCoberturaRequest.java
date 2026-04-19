package com.sofka.cotizador.interfaces.rest.dto;

public record OpcionesCoberturaRequest(
        Boolean incendioEdificios,
        Boolean incendioContenidos,
        Boolean extensionCobertura,
        Boolean catTev,
        Boolean catFhm,
        Boolean remocionEscombros,
        Boolean gastosExtraordinarios,
        Boolean perdidaRentas,
        Boolean bi,
        Boolean equipoElectronico,
        Boolean robo,
        Boolean dineroValores,
        Boolean vidrios,
        Boolean anunciosLuminosos
) {}
