package com.sofka.cotizador.domain.service;

import com.sofka.cotizador.domain.model.ubicacion.AlertaBloqueante;
import com.sofka.cotizador.domain.model.ubicacion.CodigoAlertaBloqueante;
import com.sofka.cotizador.domain.model.ubicacion.EstadoValidacionUbicacion;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;

import java.util.ArrayList;
import java.util.List;

public class ValidadorUbicacion {

    public record ResultadoValidacion(List<AlertaBloqueante> alertas, EstadoValidacionUbicacion estado) {}

    public ResultadoValidacion validar(Ubicacion ubicacion) {
        List<AlertaBloqueante> alertas = new ArrayList<>();

        if (ubicacion.getZonaCatastrofica() == null) {
            alertas.add(new AlertaBloqueante(
                    CodigoAlertaBloqueante.CODIGO_POSTAL_INVALIDO.name(),
                    "El código postal no es válido o no fue encontrado en el catálogo",
                    "codigoPostal"
            ));
        }

        if (ubicacion.getGiro() == null || isBlank(ubicacion.getGiro().claveIncendio())) {
            alertas.add(new AlertaBloqueante(
                    CodigoAlertaBloqueante.FALTA_CLAVE_INCENDIO.name(),
                    "El giro no tiene clave de incendio asignada",
                    "giro.claveIncendio"
            ));
        }

        if (ubicacion.getGarantias() == null || ubicacion.getGarantias().isEmpty()) {
            alertas.add(new AlertaBloqueante(
                    CodigoAlertaBloqueante.SIN_GARANTIAS_TARIFABLES.name(),
                    "La ubicación debe tener al menos una garantía tarifable",
                    "garantias"
            ));
        }

        if (ubicacion.getZonaCatastrofica() != null
                && (isBlank(ubicacion.getZonaCatastrofica().zonaTev())
                    || isBlank(ubicacion.getZonaCatastrofica().zonaFhm()))) {
            alertas.add(new AlertaBloqueante(
                    CodigoAlertaBloqueante.ZONA_SIN_TARIFA.name(),
                    "La zona catastrófica no tiene tarifa completa (zonaTev o zonaFhm ausente)",
                    "zonaCatastrofica"
            ));
        }

        if (ubicacion.getGiro() != null && isBlank(ubicacion.getGiro().codigo())) {
            alertas.add(new AlertaBloqueante(
                    CodigoAlertaBloqueante.GIRO_NO_CATALOGADO.name(),
                    "El código de giro no está catalogado",
                    "giro.codigo"
            ));
        }

        if (isBlank(ubicacion.getTipoConstructivo())) {
            alertas.add(new AlertaBloqueante(
                    CodigoAlertaBloqueante.TIPO_CONSTRUCTIVO_INVALIDO.name(),
                    "El tipo constructivo es obligatorio",
                    "tipoConstructivo"
            ));
        }

        EstadoValidacionUbicacion estado = alertas.isEmpty()
                ? EstadoValidacionUbicacion.VALIDO
                : EstadoValidacionUbicacion.INCOMPLETO;

        return new ResultadoValidacion(alertas, estado);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
