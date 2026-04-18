package com.sofka.cotizador.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

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
    private final DatosGenerales datosGenerales;

    public Folio actualizarDatosGenerales(DatosGenerales datos) {
        if (Objects.equals(datos, this.datosGenerales)) {
            return this;
        }
        return Folio.builder()
                .id(this.id)
                .numeroFolio(this.numeroFolio)
                .idempotencyKey(this.idempotencyKey)
                .estado(this.estado)
                .tipoNegocio(this.tipoNegocio)
                .codigoAgente(this.codigoAgente)
                .version(this.version + 1)
                .fechaCreacion(this.fechaCreacion)
                .fechaUltimaActualizacion(LocalDateTime.now())
                .datosGenerales(datos)
                .build();
    }
}
