package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.calculo.ResultadoCalculo;

public record EjecutarCalculoResult(ResultadoCalculo resultado, Cotizacion cotizacionActualizada) {}
