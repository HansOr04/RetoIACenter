package com.sofka.cotizador.domain.model;

public record SeccionesAplican(
        boolean direccion,
        boolean datosTecnicos,
        boolean giroComercial,
        boolean garantias
) {}
