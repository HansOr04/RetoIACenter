package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.model.LayoutUbicaciones;

public record ActualizarLayoutCommand(String numeroFolio, LayoutUbicaciones layout) {}
