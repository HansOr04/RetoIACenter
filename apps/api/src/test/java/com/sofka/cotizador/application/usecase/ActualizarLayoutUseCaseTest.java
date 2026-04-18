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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActualizarLayoutUseCaseTest {

    @Mock
    private FolioRepository folioRepository;

    private ActualizarLayoutUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ActualizarLayoutUseCase(folioRepository);
    }

    @Test
    void debeActualizarLayoutYIncrementarVersion() {
        Folio folioExistente = folioBase().layoutUbicaciones(null).version(1).build();
        LayoutUbicaciones layout = layoutSample();
        Folio folioActualizado = folioBase().layoutUbicaciones(layout).version(2).build();

        when(folioRepository.findByNumeroFolio("F2026-0001")).thenReturn(Optional.of(folioExistente));
        when(folioRepository.save(any(Folio.class))).thenReturn(folioActualizado);

        Folio result = useCase.ejecutar(new ActualizarLayoutCommand("F2026-0001", layout));

        assertThat(result.getVersion()).isEqualTo(2);
        assertThat(result.getLayoutUbicaciones()).isEqualTo(layout);
        verify(folioRepository).save(any(Folio.class));
    }

    @Test
    void debeRetornarMismoFolioCuandoLayoutEsIdentico() {
        LayoutUbicaciones layout = layoutSample();
        Folio folioConLayout = folioBase().layoutUbicaciones(layout).version(2).build();

        when(folioRepository.findByNumeroFolio("F2026-0001")).thenReturn(Optional.of(folioConLayout));
        when(folioRepository.save(any(Folio.class))).thenReturn(folioConLayout);

        Folio result = useCase.ejecutar(new ActualizarLayoutCommand("F2026-0001", layout));

        assertThat(result.getVersion()).isEqualTo(2);
        // actualizarLayoutUbicaciones returns same object when layout is identical — save still called
        verify(folioRepository).save(argThat(f -> f.getVersion().equals(2)));
    }

    @Test
    void debeLanzarFolioNotFoundCuandoFolioNoExiste() {
        when(folioRepository.findByNumeroFolio("INEXISTENTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.ejecutar(new ActualizarLayoutCommand("INEXISTENTE", layoutSample())))
                .isInstanceOf(FolioNotFoundException.class)
                .hasMessageContaining("INEXISTENTE");

        verify(folioRepository, never()).save(any());
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
