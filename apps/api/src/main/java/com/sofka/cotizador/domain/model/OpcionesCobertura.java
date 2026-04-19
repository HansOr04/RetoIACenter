package com.sofka.cotizador.domain.model;

import com.sofka.cotizador.domain.exception.CoberturaReglaVioladaException;

public record OpcionesCobertura(
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
        boolean anunciosLuminosos
) {
    public OpcionesCobertura {
        boolean algunaActiva = incendioEdificios || incendioContenidos
                || extensionCobertura || catTev || catFhm || remocionEscombros
                || gastosExtraordinarios || perdidaRentas || bi || equipoElectronico
                || robo || dineroValores || vidrios || anunciosLuminosos;
        if (!algunaActiva) {
            throw new CoberturaReglaVioladaException("Al menos una cobertura debe estar activa");
        }
    }

    public static OpcionesCobertura defaults() {
        return new OpcionesCobertura(
                true, false, false, false, false, false,
                false, false, false, false, false, false, false, false
        );
    }
}
