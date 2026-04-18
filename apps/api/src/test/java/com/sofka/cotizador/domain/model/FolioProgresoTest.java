package com.sofka.cotizador.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FolioProgresoTest {

    @Test
    void folioRecienCreadoTiene0PorcentoYNoEsCalculable() {
        Folio folio = folioBase().build();

        ProgresoCotizacion progreso = folio.calcularProgreso();

        assertThat(progreso.porcentajeProgreso()).isEqualTo(0);
        assertThat(progreso.esCalculable()).isFalse();
        assertThat(progreso.alertas()).hasSize(2);
    }

    @Test
    void folioConDatosGeneralesTiene50Porcento() {
        DatosGenerales datos = new DatosGenerales(
                "Test SA", "1791234560001", "a@b.com", null,
                "CASA", "HABITACIONAL", null, null, null);
        Folio folio = folioBase().datosGenerales(datos).build();

        ProgresoCotizacion progreso = folio.calcularProgreso();

        assertThat(progreso.porcentajeProgreso()).isEqualTo(50);
        assertThat(progreso.esCalculable()).isFalse();
        assertThat(progreso.alertas()).hasSize(1);
        assertThat(progreso.alertas().get(0).seccion()).isEqualTo("layoutUbicaciones");
    }

    @Test
    void folioConTodasLasSeccionesTiene100PorcentoYEsCalculable() {
        DatosGenerales datos = new DatosGenerales(
                "Test SA", "1791234560001", "a@b.com", null,
                "CASA", "HABITACIONAL", null, null, null);
        LayoutUbicaciones layout = new LayoutUbicaciones(
                2, new SeccionesAplican(true, false, false, false));
        Folio folio = folioBase().datosGenerales(datos).layoutUbicaciones(layout).build();

        ProgresoCotizacion progreso = folio.calcularProgreso();

        assertThat(progreso.porcentajeProgreso()).isEqualTo(100);
        assertThat(progreso.esCalculable()).isTrue();
        assertThat(progreso.alertas()).isEmpty();
    }

    @Test
    void seccionesCompletadasContieneTodasLasClaves() {
        Folio folio = folioBase().build();

        ProgresoCotizacion progreso = folio.calcularProgreso();

        assertThat(progreso.seccionesCompletadas()).containsKeys("datosGenerales", "layoutUbicaciones");
        assertThat(progreso.seccionesCompletadas().get("datosGenerales")).isFalse();
        assertThat(progreso.seccionesCompletadas().get("layoutUbicaciones")).isFalse();
    }

    private Folio.FolioBuilder folioBase() {
        return Folio.builder()
                .id(UUID.randomUUID().toString())
                .numeroFolio("F2026-0001")
                .idempotencyKey("key-001")
                .estado(EstadoCotizacion.BORRADOR)
                .version(1)
                .fechaCreacion(LocalDateTime.now())
                .fechaUltimaActualizacion(LocalDateTime.now());
    }
}
