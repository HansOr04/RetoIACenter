package com.sofka.cotizador.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

// HU-001 — agregado raíz del dominio; sin anotaciones de framework
@Builder
@Getter
@ToString
@EqualsAndHashCode(of = "id")
public class Folio {

    private final String id;
    private final String numeroFolio;
    private final String idempotencyKey;
    private final EstadoCotizacion estado;
    private final String tipoNegocio;
    private final String codigoAgente;
    private final Integer version;
    private final LocalDateTime fechaCreacion;
    private final LocalDateTime fechaUltimaActualizacion;
}
