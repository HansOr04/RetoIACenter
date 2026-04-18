package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.model.Folio;

// HU-001 — distingue 201 (nuevo) de 200 (idempotente)
public record CrearFolioResult(Folio folio, boolean creado) {}
