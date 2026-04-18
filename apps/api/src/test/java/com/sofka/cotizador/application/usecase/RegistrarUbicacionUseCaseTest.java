package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.exception.LayoutCapacityExceededException;
import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.EstadoCotizacion;
import com.sofka.cotizador.domain.model.Folio;
import com.sofka.cotizador.domain.model.LayoutUbicaciones;
import com.sofka.cotizador.domain.model.SeccionesAplican;
import com.sofka.cotizador.domain.model.ubicacion.CodigoAlertaBloqueante;
import com.sofka.cotizador.domain.model.ubicacion.Giro;
import com.sofka.cotizador.domain.model.ubicacion.ZonaCatastrofica;
import com.sofka.cotizador.domain.port.CotizacionRepository;
import com.sofka.cotizador.domain.port.FolioRepository;
import com.sofka.cotizador.domain.port.ValidadorCodigoPostalService;
import com.sofka.cotizador.domain.service.ValidadorUbicacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarUbicacionUseCaseTest {

    @Mock
    private FolioRepository folioRepository;

    @Mock
    private CotizacionRepository cotizacionRepository;

    @Mock
    private ValidadorCodigoPostalService validadorCodigoPostalService;

    private ValidadorUbicacion validadorUbicacion;
    private RegistrarUbicacionUseCase useCase;

    @BeforeEach
    void setUp() {
        validadorUbicacion = new ValidadorUbicacion();
        useCase = new RegistrarUbicacionUseCase(
                folioRepository, cotizacionRepository,
                validadorCodigoPostalService, validadorUbicacion);
    }

    @Test
    void debeRegistrarUbicacionExitosamente() {
        Folio folio = folioConLayout("F2026-0001", 5);
        Cotizacion cotizacionVacia = new Cotizacion("F2026-0001", Collections.emptyList(), 1, LocalDateTime.now());
        ZonaCatastrofica zona = new ZonaCatastrofica("TEV-A", "FHM-1");

        when(folioRepository.findByNumeroFolio("F2026-0001")).thenReturn(Optional.of(folio));
        when(cotizacionRepository.findByNumeroFolio("F2026-0001")).thenReturn(Optional.of(cotizacionVacia));
        when(validadorCodigoPostalService.validarCodigoPostal("090001")).thenReturn(Optional.of(zona));
        when(cotizacionRepository.save(any(Cotizacion.class))).thenAnswer(inv -> inv.getArgument(0));

        RegistrarUbicacionCommand command = comandoValido("F2026-0001");
        Cotizacion resultado = useCase.ejecutar(command);

        assertThat(resultado.getUbicaciones()).hasSize(1);
        assertThat(resultado.getUbicaciones().get(0).getIndice()).isEqualTo(0);
        assertThat(resultado.getUbicaciones().get(0).getZonaCatastrofica()).isNotNull();
    }

    @Test
    void debeLanzarExcepcionCuandoCapacidadExcedida() {
        Folio folio = folioConLayout("F2026-0002", 1);
        Cotizacion cotizacionLlena = cotizacionConUnaUbicacion("F2026-0002");

        when(folioRepository.findByNumeroFolio("F2026-0002")).thenReturn(Optional.of(folio));
        when(cotizacionRepository.findByNumeroFolio("F2026-0002")).thenReturn(Optional.of(cotizacionLlena));

        RegistrarUbicacionCommand command = comandoValido("F2026-0002");

        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(LayoutCapacityExceededException.class);
    }

    @Test
    void debeLanzarExcepcionCuandoFolioNoExiste() {
        when(folioRepository.findByNumeroFolio("F9999-0000")).thenReturn(Optional.empty());

        RegistrarUbicacionCommand command = comandoValido("F9999-0000");

        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(FolioNotFoundException.class);
    }

    @Test
    void debeRegistrarUbicacionConAlertaCuandoCPInvalido() {
        Folio folio = folioConLayout("F2026-0003", 5);
        Cotizacion cotizacionVacia = new Cotizacion("F2026-0003", Collections.emptyList(), 1, LocalDateTime.now());

        when(folioRepository.findByNumeroFolio("F2026-0003")).thenReturn(Optional.of(folio));
        when(cotizacionRepository.findByNumeroFolio("F2026-0003")).thenReturn(Optional.of(cotizacionVacia));
        when(validadorCodigoPostalService.validarCodigoPostal("000000")).thenReturn(Optional.empty());
        when(cotizacionRepository.save(any(Cotizacion.class))).thenAnswer(inv -> inv.getArgument(0));

        RegistrarUbicacionCommand command = new RegistrarUbicacionCommand(
                "F2026-0003", "Bodega", "Av. Test", "000000",
                null, null, null, null,
                "MAMPOSTERIA", null, null,
                new RegistrarUbicacionCommand.GiroCommand("G001", "Comercio", "INC-01"),
                List.of("INCENDIO")
        );

        Cotizacion resultado = useCase.ejecutar(command);

        assertThat(resultado.getUbicaciones()).hasSize(1);
        List<String> codigos = resultado.getUbicaciones().get(0).getAlertasBloqueantes().stream()
                .map(com.sofka.cotizador.domain.model.ubicacion.AlertaBloqueante::codigo)
                .toList();
        assertThat(codigos).contains(CodigoAlertaBloqueante.CODIGO_POSTAL_INVALIDO.name());
    }

    private Folio folioConLayout(String numeroFolio, int maxUbicaciones) {
        LayoutUbicaciones layout = new LayoutUbicaciones(
                maxUbicaciones,
                new SeccionesAplican(true, true, true, true)
        );
        return Folio.builder()
                .id(UUID.randomUUID().toString())
                .numeroFolio(numeroFolio)
                .estado(EstadoCotizacion.BORRADOR)
                .version(1)
                .fechaCreacion(LocalDateTime.now())
                .fechaUltimaActualizacion(LocalDateTime.now())
                .layoutUbicaciones(layout)
                .build();
    }

    private Cotizacion cotizacionConUnaUbicacion(String numeroFolio) {
        com.sofka.cotizador.domain.model.ubicacion.Ubicacion u =
                com.sofka.cotizador.domain.model.ubicacion.Ubicacion.builder()
                        .indice(0)
                        .nombreUbicacion("Existente")
                        .codigoPostal("090001")
                        .build();
        return new Cotizacion(numeroFolio, List.of(u), 2, LocalDateTime.now());
    }

    private RegistrarUbicacionCommand comandoValido(String numeroFolio) {
        return new RegistrarUbicacionCommand(
                numeroFolio,
                "Bodega Principal",
                "Av. Principal 123",
                "090001",
                "Guayas", "Guayaquil", "Centro", "Guayaquil",
                "MAMPOSTERIA",
                1, 2000,
                new RegistrarUbicacionCommand.GiroCommand("G001", "Comercio General", "INC-01"),
                List.of("INCENDIO", "ROBO")
        );
    }
}
