package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.model.Folio;
import com.sofka.cotizador.domain.model.ProgresoCotizacion;

public record EstadoFolioResult(Folio folio, ProgresoCotizacion progreso) {}
