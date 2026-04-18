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

@ExtendWith(MockitoExtension.class)
class ConsultarDatosGeneralesUseCaseTest {

    @Mock
    private FolioRepository folioRepository;

    private ConsultarDatosGeneralesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConsultarDatosGeneralesUseCase(folioRepository);
    }

    @Test
    void debeRetornarFolioConDatosGenerales() {
        DatosGenerales datos = datosSample();
        Folio folio = folioBase().datosGenerales(datos).build();

        org.mockito.Mockito.when(folioRepository.findByNumeroFolio("F2026-0001"))
                .thenReturn(Optional.of(folio));

        Folio result = useCase.ejecutar(new ConsultarDatosGeneralesCommand("F2026-0001"));

        assertThat(result.getDatosGenerales()).isNotNull();
        assertThat(result.getDatosGenerales().nombreTomador()).isEqualTo("Empresa Test S.A.");
    }

    @Test
    void debeRetornarFolioSinDatosCuandoNuncaSeGuardaron() {
        Folio folio = folioBase().datosGenerales(null).build();

        org.mockito.Mockito.when(folioRepository.findByNumeroFolio("F2026-0001"))
                .thenReturn(Optional.of(folio));

        Folio result = useCase.ejecutar(new ConsultarDatosGeneralesCommand("F2026-0001"));

        assertThat(result.getDatosGenerales()).isNull();
    }

    @Test
    void debeLanzarFolioNotFoundCuandoFolioNoExiste() {
        org.mockito.Mockito.when(folioRepository.findByNumeroFolio("INEXISTENTE"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.ejecutar(new ConsultarDatosGeneralesCommand("INEXISTENTE")))
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
