package com.sofka.cotizador.application.usecase;

import java.util.List;

public record RegistrarUbicacionCommand(
        String numeroFolio,
        String nombreUbicacion,
        String direccion,
        String codigoPostal,
        String estado,
        String municipio,
        String colonia,
        String ciudad,
        String tipoConstructivo,
        Integer nivel,
        Integer anioConstruccion,
        GiroCommand giro,
        List<String> garantias
) {
    public record GiroCommand(String codigo, String descripcion, String claveIncendio) {}
}
