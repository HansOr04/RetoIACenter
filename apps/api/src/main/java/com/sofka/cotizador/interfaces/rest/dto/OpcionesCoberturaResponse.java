package com.sofka.cotizador.interfaces.rest.dto;

import java.util.List;

public record OpcionesCoberturaResponse(
        boolean incendioEdificios,
        boolean incendioContenidos,
        boolean extensionCobertura,
        boolean catTev,
        boolean catFhm,
        boolean remocionEscombros,
        boolean gastosExtraordinarios,
        boolean perdidaRentas,
        boolean bi,
        boolean equipoElectronico,
        boolean robo,
        boolean dineroValores,
        boolean vidrios,
        boolean anunciosLuminosos,
        List<String> warnings,
        int version,
        String fechaUltimaActualizacion
) {}
