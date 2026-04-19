package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.exception.VersionConflictException;
import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.OpcionesCobertura;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;
import com.sofka.cotizador.domain.model.ubicacion.ZonaCatastrofica;
import com.sofka.cotizador.domain.port.CotizacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurarOpcionesCoberturaUseCaseTest {

    @Mock
    private CotizacionRepository cotizacionRepository;

    private ConfigurarOpcionesCoberturaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConfigurarOpcionesCoberturaUseCase(cotizacionRepository);
    }

    private static final String FOLIO = "COT-001";
    private static final OpcionesCobertura OPCIONES_VALIDAS = OpcionesCobertura.defaults();

    private Cotizacion cotizacionEnVersion(int version) {
        return new Cotizacion(FOLIO, List.of(), version, LocalDateTime.now());
    }

    @Test
    void debeGuardarOpcionesYIncrementarVersion() {
        when(cotizacionRepository.findByNumeroFolio(FOLIO))
                .thenReturn(Optional.of(cotizacionEnVersion(3)));
        when(cotizacionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ConfigurarOpcionesCoberturaResult result = useCase.ejecutar(
                new ConfigurarOpcionesCoberturaCommand(FOLIO, 3, OPCIONES_VALIDAS));

        ArgumentCaptor<Cotizacion> captor = ArgumentCaptor.forClass(Cotizacion.class);
        verify(cotizacionRepository).save(captor.capture());

        Cotizacion guardada = captor.getValue();
        assertThat(guardada.getVersion()).isEqualTo(4);
        assertThat(guardada.getOpcionesCobertura()).isEqualTo(OPCIONES_VALIDAS);
        assertThat(result.warnings()).isEmpty();
    }

    @Test
    void debeAgregarWarningCuandoCatTevSinZona() {
        Ubicacion sinZona = Ubicacion.builder()
                .indice(0)
                .nombreUbicacion("Oficina")
                .build();
        Cotizacion cotizacion = new Cotizacion(FOLIO, List.of(sinZona), 1, LocalDateTime.now());

        when(cotizacionRepository.findByNumeroFolio(FOLIO)).thenReturn(Optional.of(cotizacion));
        when(cotizacionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OpcionesCobertura conCatTev = new OpcionesCobertura(
                true, false, false, true, false, false,
                false, false, false, false, false, false, false, false);

        ConfigurarOpcionesCoberturaResult result = useCase.ejecutar(
                new ConfigurarOpcionesCoberturaCommand(FOLIO, 1, conCatTev));

        assertThat(result.warnings()).hasSize(1);
        assertThat(result.warnings().get(0)).contains("catTev activado");
    }

    @Test
    void noDebeAgregarWarningCuandoCatTevConZonaPresente() {
        Ubicacion conZona = Ubicacion.builder()
                .indice(0)
                .zonaCatastrofica(new ZonaCatastrofica("TEV-B", "FHM-2"))
                .build();
        Cotizacion cotizacion = new Cotizacion(FOLIO, List.of(conZona), 1, LocalDateTime.now());

        when(cotizacionRepository.findByNumeroFolio(FOLIO)).thenReturn(Optional.of(cotizacion));
        when(cotizacionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OpcionesCobertura conCatTev = new OpcionesCobertura(
                true, false, false, true, false, false,
                false, false, false, false, false, false, false, false);

        ConfigurarOpcionesCoberturaResult result = useCase.ejecutar(
                new ConfigurarOpcionesCoberturaCommand(FOLIO, 1, conCatTev));

        assertThat(result.warnings()).isEmpty();
    }

    @Test
    void debeLanzarVersionConflictCuandoVersionObsoleta() {
        when(cotizacionRepository.findByNumeroFolio(FOLIO))
                .thenReturn(Optional.of(cotizacionEnVersion(5)));

        assertThatThrownBy(() -> useCase.ejecutar(
                new ConfigurarOpcionesCoberturaCommand(FOLIO, 3, OPCIONES_VALIDAS)))
                .isInstanceOf(VersionConflictException.class);
    }

    @Test
    void debeLanzarFolioNotFoundCuandoNoExiste() {
        when(cotizacionRepository.findByNumeroFolio(FOLIO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar(
                new ConfigurarOpcionesCoberturaCommand(FOLIO, 1, OPCIONES_VALIDAS)))
                .isInstanceOf(FolioNotFoundException.class);
    }
}
