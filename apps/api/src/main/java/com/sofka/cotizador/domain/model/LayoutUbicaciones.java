package com.sofka.cotizador.domain.model;

public record LayoutUbicaciones(
        Integer numeroUbicaciones,
        SeccionesAplican seccionesAplican
) {
    public LayoutUbicaciones {
        if (seccionesAplican != null && !seccionesAplican.direccion()) {
            throw new IllegalArgumentException("La sección dirección es obligatoria en todas las ubicaciones");
        }
    }
}
