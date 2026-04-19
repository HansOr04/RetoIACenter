package com.sofka.cotizador.domain.model.calculo;

import com.sofka.cotizador.domain.model.ubicacion.AlertaBloqueante;

import java.math.BigDecimal;
import java.util.List;

public record PrimaPorUbicacion(
        int indice,
        boolean calculada,
        BigDecimal total,
        DesgloseComponentes desglose,
        List<AlertaBloqueante> alertas
) {
    public static PrimaPorUbicacion incalculable(int indice, List<AlertaBloqueante> alertas) {
        return new PrimaPorUbicacion(indice, false, BigDecimal.ZERO, null, alertas);
    }
}
