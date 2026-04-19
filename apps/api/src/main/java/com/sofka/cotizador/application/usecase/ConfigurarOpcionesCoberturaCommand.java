package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.model.OpcionesCobertura;

public record ConfigurarOpcionesCoberturaCommand(
        String numeroFolio,
        Integer versionEsperada,
        OpcionesCobertura opciones
) {}
