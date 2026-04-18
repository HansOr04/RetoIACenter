package com.sofka.cotizador.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private final LayoutUbicaciones layoutUbicaciones;

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
                .layoutUbicaciones(this.layoutUbicaciones)
                .build();
    }

    public ProgresoCotizacion calcularProgreso() {
        Map<String, Boolean> secciones = new LinkedHashMap<>();
        secciones.put("datosGenerales", datosGenerales != null);
        secciones.put("layoutUbicaciones", layoutUbicaciones != null);

        long completas = secciones.values().stream().filter(v -> v).count();
        int total = secciones.size();
        int porcentaje = (int) Math.round((completas * 100.0) / total);
        boolean calculable = completas == total;

        List<AlertaProgreso> alertas = new ArrayList<>();
        if (!secciones.get("datosGenerales"))
            alertas.add(new AlertaProgreso("datosGenerales",
                    "Debe completar los datos generales del tomador"));
        if (!secciones.get("layoutUbicaciones"))
            alertas.add(new AlertaProgreso("layoutUbicaciones",
                    "Debe configurar el layout de ubicaciones"));

        return new ProgresoCotizacion(porcentaje, calculable, alertas, secciones);
    }

    public Folio actualizarLayoutUbicaciones(LayoutUbicaciones layout) {
        if (Objects.equals(layout, this.layoutUbicaciones)) {
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
                .datosGenerales(this.datosGenerales)
                .layoutUbicaciones(layout)
                .build();
    }
}
