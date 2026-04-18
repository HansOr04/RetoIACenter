package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.model.EstadoCotizacion;
import com.sofka.cotizador.domain.model.Folio;
import com.sofka.cotizador.domain.model.LayoutUbicaciones;
import com.sofka.cotizador.domain.model.SeccionesAplican;
import com.sofka.cotizador.domain.port.FolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarLayoutUseCaseTest {

    @Mock
    private FolioRepository folioRepository;

    private ConsultarLayoutUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConsultarLayoutUseCase(folioRepository);
    }

    @Test
    void debeRetornarFolioConLayout() {
        LayoutUbicaciones layout = layoutSample();
        Folio folio = folioBase().layoutUbicaciones(layout).build();

        when(folioRepository.findByNumeroFolio("F2026-0001")).thenReturn(Optional.of(folio));

        Folio result = useCase.ejecutar(new ConsultarLayoutCommand("F2026-0001"));

        assertThat(result.getLayoutUbicaciones()).isNotNull();
        assertThat(result.getLayoutUbicaciones().numeroUbicaciones()).isEqualTo(5);
        assertThat(result.getLayoutUbicaciones().seccionesAplican().direccion()).isTrue();
    }

    @Test
    void debeRetornarFolioSinLayoutCuandoNuncaSeConfiguro() {
        Folio folio = folioBase().layoutUbicaciones(null).build();

        when(folioRepository.findByNumeroFolio("F2026-0001")).thenReturn(Optional.of(folio));

        Folio result = useCase.ejecutar(new ConsultarLayoutCommand("F2026-0001"));

        assertThat(result.getLayoutUbicaciones()).isNull();
    }

    @Test
    void debeLanzarFolioNotFoundCuandoFolioNoExiste() {
        when(folioRepository.findByNumeroFolio("INEXISTENTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.ejecutar(new ConsultarLayoutCommand("INEXISTENTE")))
                .isInstanceOf(FolioNotFoundException.class)
                .hasMessageContaining("INEXISTENTE");
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

    private LayoutUbicaciones layoutSample() {
        return new LayoutUbicaciones(
                5,
                new SeccionesAplican(true, true, false, false)
        );
    }
}
