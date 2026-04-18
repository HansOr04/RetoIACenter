package com.sofka.cotizador.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LayoutUbicacionesTest {

    @Test
    void debeRechazarLayoutConDireccionFalse() {
        SeccionesAplican secciones = new SeccionesAplican(false, true, true, false);

        assertThatThrownBy(() -> new LayoutUbicaciones(3, secciones))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La sección dirección es obligatoria en todas las ubicaciones");
    }

    @Test
    void debeAceptarLayoutValido() {
        SeccionesAplican secciones = new SeccionesAplican(true, false, true, false);

        LayoutUbicaciones layout = new LayoutUbicaciones(10, secciones);

        assertThat(layout.numeroUbicaciones()).isEqualTo(10);
        assertThat(layout.seccionesAplican().direccion()).isTrue();
        assertThat(layout.seccionesAplican().datosTecnicos()).isFalse();
        assertThat(layout.seccionesAplican().giroComercial()).isTrue();
        assertThat(layout.seccionesAplican().garantias()).isFalse();
    }
}
