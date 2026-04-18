package com.sofka.cotizador.interfaces.rest.dto;

// HU-001
public record CrearFolioRequest(
        String tipoNegocio,
        String codigoAgente
) {}
