package com.sofka.cotizador.domain.exception;

import com.sofka.cotizador.domain.model.calculo.PrimaPorUbicacion;

import java.util.List;

public class SinUbicacionesCalculablesException extends RuntimeException {

    private final List<PrimaPorUbicacion> primasPorUbicacion;

    public SinUbicacionesCalculablesException(List<PrimaPorUbicacion> primasPorUbicacion) {
        super("Ninguna ubicación es calculable");
        this.primasPorUbicacion = primasPorUbicacion;
    }

    public List<PrimaPorUbicacion> getPrimasPorUbicacion() {
        return primasPorUbicacion;
    }
}
