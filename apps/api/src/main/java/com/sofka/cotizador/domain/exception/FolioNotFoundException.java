package com.sofka.cotizador.domain.exception;

// Lanzada cuando se busca un folio por numeroFolio o id y no existe
public class FolioNotFoundException extends RuntimeException {

    public FolioNotFoundException(String numeroFolio) {
        super("Folio no encontrado: " + numeroFolio);
    }
}
