package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.UbicacionNotFoundException;
import com.sofka.cotizador.domain.exception.VersionConflictException;
import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.ubicacion.EstadoValidacionUbicacion;
import com.sofka.cotizador.domain.model.ubicacion.Giro;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;
import com.sofka.cotizador.domain.model.ubicacion.ZonaCatastrofica;
import com.sofka.cotizador.domain.port.CotizacionRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EditarUbicacionPuntualUseCaseTest {

    @Mock
    private CotizacionRepository cotizacionRepository;

    @Mock
    private ValidadorCodigoPostalService validadorCodigoPostalService;

    private ValidadorUbicacion validadorUbicacion;
    private EditarUbicacionPuntualUseCase useCase;

    @BeforeEach
    void setUp() {
        validadorUbicacion = new ValidadorUbicacion();
        useCase = new EditarUbicacionPuntualUseCase(
                cotizacionRepository, validadorCodigoPostalService, validadorUbicacion);
    }

    @Test
    void debeEditarUbicacionExitosamente() {
        String folio = "F2026-0001";
        Cotizacion cotizacion = cotizacionConUbicacion(folio, 2);

        when(cotizacionRepository.findByNumeroFolio(folio)).thenReturn(Optional.of(cotizacion));
        when(validadorCodigoPostalService.validarCodigoPostal("090001"))
                .thenReturn(Optional.of(new ZonaCatastrofica("TEV-A", "FHM-1")));
        when(cotizacionRepository.save(any(Cotizacion.class))).thenAnswer(inv -> inv.getArgument(0));

        EditarUbicacionCommand command = new EditarUbicacionCommand(
                folio, 0, 2,
                "Nuevo Nombre", null, null, null, null, null, null, null
        );

        Cotizacion resultado = useCase.ejecutar(command);

        assertThat(resultado.getUbicaciones().get(0).getNombreUbicacion()).isEqualTo("Nuevo Nombre");
        assertThat(resultado.getVersion()).isEqualTo(3);
    }

    @Test
    void debeLanzarVersionConflictCuandoVersionNoCoincide() {
        String folio = "F2026-0002";
        Cotizacion cotizacion = cotizacionConUbicacion(folio, 5);

        when(cotizacionRepository.findByNumeroFolio(folio)).thenReturn(Optional.of(cotizacion));

        EditarUbicacionCommand command = new EditarUbicacionCommand(
                folio, 0, 999,
                "Nombre", null, null, null, null, null, null, null
        );

        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(VersionConflictException.class)
                .hasMessageContaining("versión actual=5")
                .hasMessageContaining("versión recibida=999");
    }

    @Test
    void debeLanzarUbicacionNotFoundCuandoIndiceNoExiste() {
        String folio = "F2026-0003";
        Cotizacion cotizacion = cotizacionConUbicacion(folio, 3);

        when(cotizacionRepository.findByNumeroFolio(folio)).thenReturn(Optional.of(cotizacion));

        EditarUbicacionCommand command = new EditarUbicacionCommand(
                folio, 99, 3,
                "Nombre", null, null, null, null, null, null, null
        );

        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(UbicacionNotFoundException.class);
    }

    @Test
    void debeIgnorarCampoIndiceEnPatch() {
        String folio = "F2026-0004";
        Cotizacion cotizacion = cotizacionConUbicacion(folio, 2);

        when(cotizacionRepository.findByNumeroFolio(folio)).thenReturn(Optional.of(cotizacion));
        when(validadorCodigoPostalService.validarCodigoPostal("090001"))
                .thenReturn(Optional.of(new ZonaCatastrofica("TEV-A", "FHM-1")));
        when(cotizacionRepository.save(any(Cotizacion.class))).thenAnswer(inv -> inv.getArgument(0));

        EditarUbicacionCommand command = new EditarUbicacionCommand(
                folio, 0, 2,
                "Nuevo Nombre", null, null, null, null, null, null, null
        );

        Cotizacion resultado = useCase.ejecutar(command);

        assertThat(resultado.getUbicaciones().get(0).getIndice()).isEqualTo(0);
    }

    private Cotizacion cotizacionConUbicacion(String folio, int version) {
        Ubicacion u = Ubicacion.builder()
                .indice(0)
                .nombreUbicacion("Bodega Original")
                .direccion("Av. Test 1")
                .codigoPostal("090001")
                .tipoConstructivo("MAMPOSTERIA")
                .giro(new Giro("G001", "Comercio", "INC-01"))
                .garantias(List.of("INCENDIO"))
                .zonaCatastrofica(new ZonaCatastrofica("TEV-A", "FHM-1"))
                .estadoValidacion(EstadoValidacionUbicacion.VALIDO)
                .alertasBloqueantes(Collections.emptyList())
                .build();
        return new Cotizacion(folio, List.of(u), version, LocalDateTime.now());
    }
}
