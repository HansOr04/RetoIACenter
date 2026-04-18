package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.model.DatosGenerales;

public record ActualizarDatosGeneralesCommand(
        String numeroFolio,
        DatosGenerales datosGenerales
) {}
