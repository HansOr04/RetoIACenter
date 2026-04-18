package com.sofka.cotizador.domain.service;

import com.sofka.cotizador.domain.model.ubicacion.AlertaBloqueante;
import com.sofka.cotizador.domain.model.ubicacion.CodigoAlertaBloqueante;
import com.sofka.cotizador.domain.model.ubicacion.EstadoValidacionUbicacion;
import com.sofka.cotizador.domain.model.ubicacion.Giro;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;
import com.sofka.cotizador.domain.model.ubicacion.ZonaCatastrofica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValidadorUbicacionTest {

    private ValidadorUbicacion validador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorUbicacion();
    }

    @Test
    void debeGenerarAlertaCuandoCodigoPostalInvalido() {
        Ubicacion ubicacion = ubicacionBase()
                .zonaCatastrofica(null)
                .build();

        ValidadorUbicacion.ResultadoValidacion resultado = validador.validar(ubicacion);

        assertThat(resultado.estado()).isEqualTo(EstadoValidacionUbicacion.INCOMPLETO);
        assertThat(codigosAlertas(resultado)).contains(CodigoAlertaBloqueante.CODIGO_POSTAL_INVALIDO.name());
    }

    @Test
    void debeGenerarAlertaCuandoFaltaClaveIncendio() {
        Ubicacion ubicacion = ubicacionBase()
                .giro(new Giro("G001", "Comercio", ""))
                .build();

        ValidadorUbicacion.ResultadoValidacion resultado = validador.validar(ubicacion);

        assertThat(resultado.estado()).isEqualTo(EstadoValidacionUbicacion.INCOMPLETO);
        assertThat(codigosAlertas(resultado)).contains(CodigoAlertaBloqueante.FALTA_CLAVE_INCENDIO.name());
    }

    @Test
    void debeGenerarAlertaCuandoSinGarantias() {
        Ubicacion ubicacion = ubicacionBase()
                .garantias(List.of())
                .build();

        ValidadorUbicacion.ResultadoValidacion resultado = validador.validar(ubicacion);

        assertThat(resultado.estado()).isEqualTo(EstadoValidacionUbicacion.INCOMPLETO);
        assertThat(codigosAlertas(resultado)).contains(CodigoAlertaBloqueante.SIN_GARANTIAS_TARIFABLES.name());
    }

    @Test
    void debeGenerarAlertaCuandoZonaSinTarifa() {
        Ubicacion ubicacion = ubicacionBase()
                .zonaCatastrofica(new ZonaCatastrofica("", "FHM-1"))
                .build();

        ValidadorUbicacion.ResultadoValidacion resultado = validador.validar(ubicacion);

        assertThat(resultado.estado()).isEqualTo(EstadoValidacionUbicacion.INCOMPLETO);
        assertThat(codigosAlertas(resultado)).contains(CodigoAlertaBloqueante.ZONA_SIN_TARIFA.name());
    }

    @Test
    void debeGenerarAlertaCuandoGiroNoCatalogado() {
        Ubicacion ubicacion = ubicacionBase()
                .giro(new Giro("", "Sin código", "INC-1"))
                .build();

        ValidadorUbicacion.ResultadoValidacion resultado = validador.validar(ubicacion);

        assertThat(resultado.estado()).isEqualTo(EstadoValidacionUbicacion.INCOMPLETO);
        assertThat(codigosAlertas(resultado)).contains(CodigoAlertaBloqueante.GIRO_NO_CATALOGADO.name());
    }

    @Test
    void debeGenerarAlertaCuandoTipoConstructivoInvalido() {
        Ubicacion ubicacion = ubicacionBase()
                .tipoConstructivo("")
                .build();

        ValidadorUbicacion.ResultadoValidacion resultado = validador.validar(ubicacion);

        assertThat(resultado.estado()).isEqualTo(EstadoValidacionUbicacion.INCOMPLETO);
        assertThat(codigosAlertas(resultado)).contains(CodigoAlertaBloqueante.TIPO_CONSTRUCTIVO_INVALIDO.name());
    }

    @Test
    void debeRetornarValidoCuandoTodosLosCamposCorrectos() {
        Ubicacion ubicacion = ubicacionBase().build();

        ValidadorUbicacion.ResultadoValidacion resultado = validador.validar(ubicacion);

        assertThat(resultado.estado()).isEqualTo(EstadoValidacionUbicacion.VALIDO);
        assertThat(resultado.alertas()).isEmpty();
    }

    private Ubicacion.Builder ubicacionBase() {
        return Ubicacion.builder()
                .indice(0)
                .nombreUbicacion("Bodega Central")
                .direccion("Av. Principal 123")
                .codigoPostal("090001")
                .tipoConstructivo("MAMPOSTERIA")
                .giro(new Giro("G001", "Comercio General", "INC-01"))
                .garantias(List.of("INCENDIO", "ROBO"))
                .zonaCatastrofica(new ZonaCatastrofica("TEV-A", "FHM-1"));
    }

    private List<String> codigosAlertas(ValidadorUbicacion.ResultadoValidacion resultado) {
        return resultado.alertas().stream()
                .map(AlertaBloqueante::codigo)
                .toList();
    }
}
