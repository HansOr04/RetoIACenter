package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.CoreServiceUnavailableException;
import com.sofka.cotizador.domain.model.EstadoCotizacion;
import com.sofka.cotizador.domain.model.Folio;
import com.sofka.cotizador.domain.port.CoreFolioService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// HU-001
@ExtendWith(MockitoExtension.class)
class CrearFolioUseCaseTest {

    @Mock
    private FolioRepository folioRepository;

    @Mock
    private CoreFolioService coreFolioService;

    private CrearFolioUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CrearFolioUseCase(folioRepository, coreFolioService);
    }

    @Test
    void debeCrearNuevoFolioCuandoKeyNueva() {
        CrearFolioCommand command = new CrearFolioCommand("key-001", "INCENDIO", "AGT-001");
        Folio folioGuardado = folioConId("F2026-0001");

        when(folioRepository.findByIdempotencyKey("key-001")).thenReturn(Optional.empty());
        when(coreFolioService.generarNumeroFolio("AGT-001")).thenReturn("F2026-0001");
        when(folioRepository.save(any(Folio.class))).thenReturn(folioGuardado);

        CrearFolioResult result = useCase.ejecutar(command);

        assertThat(result.creado()).isTrue();
        assertThat(result.folio().getNumeroFolio()).isEqualTo("F2026-0001");
        assertThat(result.folio().getEstado()).isEqualTo(EstadoCotizacion.BORRADOR);
        verify(folioRepository).save(any(Folio.class));
    }

    @Test
    void debeRetornarFolioExistenteConIdempotencia() {
        CrearFolioCommand command = new CrearFolioCommand("key-001", "INCENDIO", "AGT-001");
        Folio existing = folioConId("F2026-0001");

        when(folioRepository.findByIdempotencyKey("key-001")).thenReturn(Optional.of(existing));

        CrearFolioResult result = useCase.ejecutar(command);

        assertThat(result.creado()).isFalse();
        assertThat(result.folio().getNumeroFolio()).isEqualTo("F2026-0001");
        verify(coreFolioService, never()).generarNumeroFolio(anyString());
        verify(folioRepository, never()).save(any());
    }

    @Test
    void debePropagarcoreServiceUnavailableException() {
        CrearFolioCommand command = new CrearFolioCommand("key-002", null, null);

        when(folioRepository.findByIdempotencyKey("key-002")).thenReturn(Optional.empty());
        when(coreFolioService.generarNumeroFolio(null))
                .thenThrow(new CoreServiceUnavailableException("Core no disponible"));

        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(CoreServiceUnavailableException.class)
                .hasMessageContaining("Core no disponible");

        verify(folioRepository, never()).save(any());
    }

    private Folio folioConId(String numeroFolio) {
        return Folio.builder()
                .id(UUID.randomUUID().toString())
                .numeroFolio(numeroFolio)
                .idempotencyKey("key-001")
                .estado(EstadoCotizacion.BORRADOR)
                .version(1)
                .fechaCreacion(LocalDateTime.now())
                .fechaUltimaActualizacion(LocalDateTime.now())
                .build();
    }
}
