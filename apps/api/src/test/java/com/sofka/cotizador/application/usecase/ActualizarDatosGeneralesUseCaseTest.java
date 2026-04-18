package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.model.DatosGenerales;
import com.sofka.cotizador.domain.model.EstadoCotizacion;
import com.sofka.cotizador.domain.model.Folio;
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
class ActualizarDatosGeneralesUseCaseTest {

    @Mock
    private FolioRepository folioRepository;

    private ActualizarDatosGeneralesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ActualizarDatosGeneralesUseCase(folioRepository);
    }

    @Test
    void debeActualizarDatosYIncrementarVersion() {
        Folio folioExistente = folioBase().datosGenerales(null).version(1).build();
        DatosGenerales datos = datosSample();
        Folio folioActualizado = folioBase().datosGenerales(datos).version(2).build();

        when(folioRepository.findByNumeroFolio("F2026-0001")).thenReturn(Optional.of(folioExistente));
        when(folioRepository.save(any(Folio.class))).thenReturn(folioActualizado);

        Folio result = useCase.ejecutar(new ActualizarDatosGeneralesCommand("F2026-0001", datos));

        assertThat(result.getVersion()).isEqualTo(2);
        assertThat(result.getDatosGenerales()).isEqualTo(datos);
        verify(folioRepository).save(any(Folio.class));
    }

    @Test
    void debeRetornarMismoFolioCuandoDatosSonIdenticos() {
        DatosGenerales datos = datosSample();
        Folio folioConDatos = folioBase().datosGenerales(datos).version(2).build();

        when(folioRepository.findByNumeroFolio("F2026-0001")).thenReturn(Optional.of(folioConDatos));
        when(folioRepository.save(any(Folio.class))).thenReturn(folioConDatos);

        Folio result = useCase.ejecutar(new ActualizarDatosGeneralesCommand("F2026-0001", datos));

        assertThat(result.getVersion()).isEqualTo(2);
        // actualizarDatosGenerales devuelve el mismo objeto cuando datos son iguales
        verify(folioRepository).save(argThat(f -> f.getVersion().equals(2)));
    }

    @Test
    void debeLanzarFolioNotFoundCuandoFolioNoExiste() {
        when(folioRepository.findByNumeroFolio("INEXISTENTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.ejecutar(new ActualizarDatosGeneralesCommand("INEXISTENTE", datosSample())))
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

    private DatosGenerales datosSample() {
        return new DatosGenerales(
                "Empresa Test S.A.",
                "1792345678001",
                "test@empresa.com",
                "0991234567",
                "LOCAL_COMERCIAL",
                "COMERCIAL",
                2010,
                3,
                "Local en planta baja"
        );
    }
}
