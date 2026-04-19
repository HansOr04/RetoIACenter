package com.sofka.cotizador.domain.model;

import com.sofka.cotizador.domain.exception.CoberturaReglaVioladaException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpcionesCoberturaTest {

    @Test
    void debeRechazarCuandoTodasLasCoberturasSonFalse() {
        assertThatThrownBy(() -> new OpcionesCobertura(
                false, false, false, false, false, false,
                false, false, false, false, false, false, false, false
        ))
                .isInstanceOf(CoberturaReglaVioladaException.class)
                .hasMessageContaining("Al menos una cobertura debe estar activa");
    }

    @Test
    void debeAceptarCuandoAlMenosUnaEsTrue() {
        OpcionesCobertura opciones = new OpcionesCobertura(
                true, false, false, false, false, false,
                false, false, false, false, false, false, false, false
        );
        assertThat(opciones.incendioEdificios()).isTrue();
    }

    @Test
    void defaultsDebenTenerIncendioEdificiosTrue() {
        OpcionesCobertura defaults = OpcionesCobertura.defaults();
        assertThat(defaults.incendioEdificios()).isTrue();
        assertThat(defaults.incendioContenidos()).isFalse();
        assertThat(defaults.catTev()).isFalse();
        assertThat(defaults.catFhm()).isFalse();
    }
}
