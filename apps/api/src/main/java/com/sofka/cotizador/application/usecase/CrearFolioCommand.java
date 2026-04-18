package com.sofka.cotizador.application.usecase;

// HU-001
public record CrearFolioCommand(
        String idempotencyKey,
        String tipoNegocio,
        String codigoAgente
) {}
