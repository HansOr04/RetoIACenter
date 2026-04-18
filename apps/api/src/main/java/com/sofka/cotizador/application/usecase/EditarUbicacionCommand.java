package com.sofka.cotizador.application.usecase;

import java.util.List;

public record EditarUbicacionCommand(
        String numeroFolio,
        int indice,
        int versionEsperada,
        String nombreUbicacion,
        String direccion,
        String codigoPostal,
        String tipoConstructivo,
        Integer nivel,
        Integer anioConstruccion,
        GiroCommand giro,
        List<String> garantias
) {
    public record GiroCommand(String codigo, String descripcion, String claveIncendio) {}
}
