package com.sofka.cotizador.interfaces.rest.dto;

import com.sofka.cotizador.domain.model.ubicacion.AlertaBloqueante;

import java.util.List;

public record UbicacionResponse(
        int indice,
        String nombreUbicacion,
        String direccion,
        String codigoPostal,
        ZonaCatastroficaData zonaCatastrofica,
        String tipoConstructivo,
        Integer nivel,
        Integer anioConstruccion,
        GiroData giro,
        List<String> garantias,
        List<AlertaBloqueante> alertasBloqueantes,
        String estadoValidacion,
        int version,
        String fechaUltimaActualizacion
) {}
