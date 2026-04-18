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
class ConsultarEstadoFolioUseCaseTest {

    @Mock
    private FolioRepository folioRepository;

    private ConsultarEstadoFolioUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConsultarEstadoFolioUseCase(folioRepository);
    }

    @Test
    void debeRetornarEstadoConProgresoCorrecto() {
        LayoutUbicaciones layout = new LayoutUbicaciones(
                3, new SeccionesAplican(true, true, false, false));
        Folio folio = folioBase().layoutUbicaciones(layout).build();

        when(folioRepository.findByNumeroFolio("F2026-0001")).thenReturn(Optional.of(folio));

        EstadoFolioResult result = useCase.ejecutar(new ConsultarEstadoFolioCommand("F2026-0001"));

        assertThat(result.folio().getNumeroFolio()).isEqualTo("F2026-0001");
        assertThat(result.progreso().porcentajeProgreso()).isEqualTo(50);
        assertThat(result.progreso().esCalculable()).isFalse();
        assertThat(result.progreso().alertas()).hasSize(1);
        assertThat(result.progreso().alertas().get(0).seccion()).isEqualTo("datosGenerales");
    }

    @Test
    void debeLanzarFolioNotFoundCuandoFolioNoExiste() {
        when(folioRepository.findByNumeroFolio("INEXISTENTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.ejecutar(new ConsultarEstadoFolioCommand("INEXISTENTE")))
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
}
