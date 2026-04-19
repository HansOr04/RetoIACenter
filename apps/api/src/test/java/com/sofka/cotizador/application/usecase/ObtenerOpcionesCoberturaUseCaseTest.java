package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.OpcionesCobertura;
import com.sofka.cotizador.domain.port.CotizacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerOpcionesCoberturaUseCaseTest {

    @Mock
    private CotizacionRepository cotizacionRepository;

    private ObtenerOpcionesCoberturaUseCase useCase;

    private static final String FOLIO = "COT-001";

    @BeforeEach
    void setUp() {
        useCase = new ObtenerOpcionesCoberturaUseCase(cotizacionRepository);
    }

    @Test
    void debeRetornarDefaultsCuandoNoConfigurado() {
        Cotizacion sinOpciones = new Cotizacion(FOLIO, List.of(), 1, LocalDateTime.now());
        when(cotizacionRepository.findByNumeroFolio(FOLIO)).thenReturn(Optional.of(sinOpciones));

        ObtenerOpcionesCoberturaUseCase.Result result =
                useCase.ejecutar(new ObtenerOpcionesCoberturaCommand(FOLIO));

        assertThat(result.opciones().incendioEdificios()).isTrue();
        assertThat(result.opciones().incendioContenidos()).isFalse();
        assertThat(result.version()).isEqualTo(1);
    }

    @Test
    void debeRetornarOpcionesExistentes() {
        OpcionesCobertura opciones = new OpcionesCobertura(
                true, true, false, false, false, false,
                false, false, false, false, false, false, false, false);
        Cotizacion conOpciones = new Cotizacion(
                FOLIO, List.of(), 2, LocalDateTime.now(), opciones, null, null);
        when(cotizacionRepository.findByNumeroFolio(FOLIO)).thenReturn(Optional.of(conOpciones));

        ObtenerOpcionesCoberturaUseCase.Result result =
                useCase.ejecutar(new ObtenerOpcionesCoberturaCommand(FOLIO));

        assertThat(result.opciones().incendioEdificios()).isTrue();
        assertThat(result.opciones().incendioContenidos()).isTrue();
        assertThat(result.version()).isEqualTo(2);
    }

    @Test
    void debeLanzarFolioNotFoundCuandoNoExiste() {
        when(cotizacionRepository.findByNumeroFolio(FOLIO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar(new ObtenerOpcionesCoberturaCommand(FOLIO)))
                .isInstanceOf(FolioNotFoundException.class);
    }
}
