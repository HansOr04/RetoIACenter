package com.sofka.cotizador.domain.exception;

public class LayoutCapacityExceededException extends RuntimeException {

    public LayoutCapacityExceededException(String numeroFolio, int maxUbicaciones) {
        super("El folio " + numeroFolio + " ya alcanzó la capacidad máxima de " + maxUbicaciones + " ubicaciones");
    }
}
