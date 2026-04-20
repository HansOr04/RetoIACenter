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
        when(cotizacionRepository.save(any(Cotizacion.class))).thenAnswer(inv -> inv.getArgument(0));

        EditarUbicacionCommand command = new EditarUbicacionCommand(
                folio, 0, 2,
                "Nuevo Nombre", null, null, null, null, null, null, null
        );

        Cotizacion resultado = useCase.ejecutar(command);

        assertThat(resultado.getUbicaciones().get(0).getIndice()).isEqualTo(0);
    }

    @Test
    void debeLanzarFolioNotFoundCuandoNoExisteFolio() {
        String folio = "F2026-9999";
        when(cotizacionRepository.findByNumeroFolio(folio)).thenReturn(Optional.empty());

        EditarUbicacionCommand command = new EditarUbicacionCommand(
                folio, 0, 1,
                "Nombre", null, null, null, null, null, null, null
        );

        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(com.sofka.cotizador.domain.exception.FolioNotFoundException.class);
    }

    @Test
    void debeActualizarTodosLosCamposYValidarCodigoPostal() {
        String folio = "F2026-0005";
        Cotizacion cotizacion = cotizacionConUbicacion(folio, 1);

        when(cotizacionRepository.findByNumeroFolio(folio)).thenReturn(Optional.of(cotizacion));
        when(cotizacionRepository.save(any(Cotizacion.class))).thenAnswer(inv -> inv.getArgument(0));

        ZonaCatastrofica nuevaZona = new ZonaCatastrofica("TEV-B", "FHM-2");
        when(validadorCodigoPostalService.validarCodigoPostal("090002"))
                .thenReturn(Optional.of(nuevaZona));

        EditarUbicacionCommand.GiroCommand giroCmd = new EditarUbicacionCommand.GiroCommand("G002", "Industria", "INC-02");
        EditarUbicacionCommand command = new EditarUbicacionCommand(
                folio, 0, 1,
                null, "Nueva Direccion", "090002", "MADERA", 2, 2020, giroCmd, List.of("INCENDIO", "TERREMOTO")
        );

        Cotizacion resultado = useCase.ejecutar(command);
        Ubicacion u = resultado.getUbicaciones().get(0);

        assertThat(u.getDireccion()).isEqualTo("Nueva Direccion");
        assertThat(u.getCodigoPostal()).isEqualTo("090002");
        assertThat(u.getTipoConstructivo()).isEqualTo("MADERA");
        assertThat(u.getNivel()).isEqualTo(2);
        assertThat(u.getAnioConstruccion()).isEqualTo(2020);
        assertThat(u.getGiro().codigo()).isEqualTo("G002");
        assertThat(u.getGarantias()).containsExactly("INCENDIO", "TERREMOTO");
        assertThat(u.getZonaCatastrofica().zonaTev()).isEqualTo("TEV-B");
    }

    @Test
    void debeAsignarNullAZonaCatastroficaSiCodigoPostalNoEsEncontrado() {
        String folio = "F2026-0006";
        Cotizacion cotizacion = cotizacionConUbicacion(folio, 1);

        when(cotizacionRepository.findByNumeroFolio(folio)).thenReturn(Optional.of(cotizacion));
        when(cotizacionRepository.save(any(Cotizacion.class))).thenAnswer(inv -> inv.getArgument(0));

        when(validadorCodigoPostalService.validarCodigoPostal("999999"))
                .thenReturn(Optional.empty());

        EditarUbicacionCommand command = new EditarUbicacionCommand(
                folio, 0, 1,
                null, null, "999999", null, null, null, null, null
        );

        Cotizacion resultado = useCase.ejecutar(command);
        Ubicacion u = resultado.getUbicaciones().get(0);

        assertThat(u.getCodigoPostal()).isEqualTo("999999");
        assertThat(u.getZonaCatastrofica()).isNull();
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
