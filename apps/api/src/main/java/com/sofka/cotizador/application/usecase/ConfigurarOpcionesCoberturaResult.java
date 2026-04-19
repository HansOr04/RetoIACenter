package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.model.Cotizacion;

import java.util.List;

public record ConfigurarOpcionesCoberturaResult(Cotizacion cotizacion, List<String> warnings) {}
