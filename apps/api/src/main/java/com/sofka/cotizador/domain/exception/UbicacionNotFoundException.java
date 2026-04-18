package com.sofka.cotizador.domain.exception;

public class UbicacionNotFoundException extends RuntimeException {

    public UbicacionNotFoundException(String numeroFolio, int indice) {
        super("Ubicación con índice " + indice + " no encontrada en folio " + numeroFolio);
    }
}
