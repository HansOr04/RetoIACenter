package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.exception.SinUbicacionesCalculablesException;
import com.sofka.cotizador.domain.exception.VersionConflictException;
import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.EstadoCotizacion;
import com.sofka.cotizador.domain.model.OpcionesCobertura;
import com.sofka.cotizador.domain.model.calculo.ParametrosCalculo;
import com.sofka.cotizador.domain.model.calculo.PrimaPorUbicacion;
import com.sofka.cotizador.domain.model.ubicacion.AlertaBloqueante;
import com.sofka.cotizador.domain.model.ubicacion.Giro;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;
import com.sofka.cotizador.domain.port.CatalogoTarifasRepository;
import com.sofka.cotizador.domain.port.CotizacionRepository;
import com.sofka.cotizador.domain.service.CalculoPrimaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EjecutarCalculoUseCaseTest {

    @Mock
    private CotizacionRepository cotizacionRepository;
    @Mock
    private CatalogoTarifasRepository catalogoTarifasRepository;
    @Mock
    private CalculoPrimaService calculoPrimaService;

    private EjecutarCalculoUseCase useCase;

    private static final String FOLIO = "COT-001";
    private static final ParametrosCalculo PARAMS = new ParametrosCalculo(
            new BigDecimal("1.25"),
            new BigDecimal("0.002"),
            new BigDecimal("0.001"),
            new BigDecimal("0.0015"),
            new BigDecimal("0.003"),
            new BigDecimal("0.0025"),
            new BigDecimal("0.004"),
            new BigDecimal("0.0018"),
            new BigDecimal("0.005")
    );

    @BeforeEach
    void setUp() {
        useCase = new EjecutarCalculoUseCase(
                cotizacionRepository, catalogoTarifasRepository, calculoPrimaService);
    }

    private Ubicacion ubicacionCalculable() {
        return Ubicacion.builder()
                .indice(0)
                .tipoConstructivo("CONCRETO_ARMADO")
                .giro(new Giro("B1", "Oficinas", "B1"))
                .build();
    }

    private Ubicacion ubicacionIncalculable() {
        return Ubicacion.builder()
                .indice(1)
                .alertasBloqueantes(List.of(
                        new AlertaBloqueante("CP_INVALIDO", "CP no válido", "codigoPostal")))
                .build();
    }

    private PrimaPorUbicacion primaCalculada(int indice) {
        return new PrimaPorUbicacion(indice, true, new BigDecimal("2000.00"), null, List.of());
    }

    private PrimaPorUbicacion primaIncalculable(int indice) {
        return PrimaPorUbicacion.incalculable(indice,
                List.of(new AlertaBloqueante("CP_INVALIDO", "CP no válido", "codigoPostal")));
    }

    @Test
    void debeCalcularYPersistirEnUnaTransaccion() {
        Cotizacion cotizacion = new Cotizacion(FOLIO, List.of(ubicacionCalculable()), 1, LocalDateTime.now());
        when(cotizacionRepository.findByNumeroFolio(FOLIO)).thenReturn(Optional.of(cotizacion));
        when(calculoPrimaService.calcularUbicacion(any(), any(), any()))
                .thenReturn(primaCalculada(0));
        when(catalogoTarifasRepository.findParametrosCalculo()).thenReturn(PARAMS);
        when(cotizacionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EjecutarCalculoResult result = useCase.ejecutar(new EjecutarCalculoCommand(FOLIO, 1));

        assertThat(result.resultado().primaNeta()).isEqualByComparingTo("2000.00");
        // primaComercial = 2000 * 1.25 = 2500
        assertThat(result.resultado().primaComercial()).isEqualByComparingTo("2500.00");
        assertThat(result.cotizacionActualizada().getEstado()).isEqualTo(EstadoCotizacion.CALCULADO);
        assertThat(result.cotizacionActualizada().getVersion()).isEqualTo(2);

        verify(cotizacionRepository, times(1)).save(any());
    }

    @Test
    void debeLanzar422CuandoTodasLasUbicacionesIncompletas() {
        Cotizacion cotizacion = new Cotizacion(FOLIO, List.of(ubicacionIncalculable()), 1, LocalDateTime.now());
        when(cotizacionRepository.findByNumeroFolio(FOLIO)).thenReturn(Optional.of(cotizacion));
        when(calculoPrimaService.calcularUbicacion(any(), any(), any()))
                .thenReturn(primaIncalculable(1));

        assertThatThrownBy(() -> useCase.ejecutar(new EjecutarCalculoCommand(FOLIO, 1)))
                .isInstanceOf(SinUbicacionesCalculablesException.class)
                .hasMessage("Ninguna ubicación es calculable");
    }

    @Test
    void debeLanzarVersionConflictCuandoVersionObsoleta() {
        Cotizacion cotizacion = new Cotizacion(FOLIO, List.of(), 5, LocalDateTime.now());
        when(cotizacionRepository.findByNumeroFolio(FOLIO)).thenReturn(Optional.of(cotizacion));

        assertThatThrownBy(() -> useCase.ejecutar(new EjecutarCalculoCommand(FOLIO, 3)))
                .isInstanceOf(VersionConflictException.class);
    }

    @Test
    void debeLanzarFolioNotFoundCuandoNoExiste() {
        when(cotizacionRepository.findByNumeroFolio(FOLIO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar(new EjecutarCalculoCommand(FOLIO, 1)))
                .isInstanceOf(FolioNotFoundException.class);
    }

    @Test
    void debeUsarDefaultsCoberturasCuandoNoConfiguradas() {
        // Cotizacion without OpcionesCobertura — should default to incendioEdificios=true
        Cotizacion cotizacion = new Cotizacion(FOLIO, List.of(ubicacionCalculable()), 1, LocalDateTime.now());
        when(cotizacionRepository.findByNumeroFolio(FOLIO)).thenReturn(Optional.of(cotizacion));
        when(catalogoTarifasRepository.findParametrosCalculo()).thenReturn(PARAMS);
        when(cotizacionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Capture which coberturas the service is called with
        when(calculoPrimaService.calcularUbicacion(any(), any(), any()))
                .thenAnswer(inv -> {
                    OpcionesCobertura coberturas = inv.getArgument(1);
                    assertThat(coberturas.incendioEdificios()).isTrue();
                    return primaCalculada(0);
                });

        useCase.ejecutar(new EjecutarCalculoCommand(FOLIO, 1));

        verify(calculoPrimaService).calcularUbicacion(any(), any(), any());
    }

    @Test
    void calcularDosVecesProduceMismoResultado() {
        // First calculation
        Cotizacion cotizacion1 = new Cotizacion(FOLIO, List.of(ubicacionCalculable()), 1, LocalDateTime.now());
        when(cotizacionRepository.findByNumeroFolio(FOLIO)).thenReturn(Optional.of(cotizacion1));
        when(calculoPrimaService.calcularUbicacion(any(), any(), any())).thenReturn(primaCalculada(0));
        when(catalogoTarifasRepository.findParametrosCalculo()).thenReturn(PARAMS);
        when(cotizacionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EjecutarCalculoResult result1 = useCase.ejecutar(new EjecutarCalculoCommand(FOLIO, 1));

        // Second calculation on the updated cotizacion
        Cotizacion cotizacion2 = result1.cotizacionActualizada();
        when(cotizacionRepository.findByNumeroFolio(FOLIO)).thenReturn(Optional.of(cotizacion2));

        EjecutarCalculoResult result2 = useCase.ejecutar(
                new EjecutarCalculoCommand(FOLIO, cotizacion2.getVersion()));

        assertThat(result1.resultado().primaNeta())
                .isEqualByComparingTo(result2.resultado().primaNeta());
        assertThat(result1.resultado().primaComercial())
                .isEqualByComparingTo(result2.resultado().primaComercial());
    }

    @Test
    void ubicacionIncalculableAparaceEnResultadoConCalculadaFalse() {
        Ubicacion calc = ubicacionCalculable();
        Ubicacion incalc = ubicacionIncalculable();

        Cotizacion cotizacion = new Cotizacion(FOLIO, List.of(calc, incalc), 1, LocalDateTime.now());
        when(cotizacionRepository.findByNumeroFolio(FOLIO)).thenReturn(Optional.of(cotizacion));
        when(calculoPrimaService.calcularUbicacion(any(), any(), any()))
                .thenReturn(primaCalculada(0), primaIncalculable(1));
        when(catalogoTarifasRepository.findParametrosCalculo()).thenReturn(PARAMS);
        when(cotizacionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EjecutarCalculoResult result = useCase.ejecutar(new EjecutarCalculoCommand(FOLIO, 1));

        List<PrimaPorUbicacion> primas = result.resultado().primasPorUbicacion();
        assertThat(primas).hasSize(2);
        assertThat(primas.stream().filter(PrimaPorUbicacion::calculada).count()).isEqualTo(1);
        assertThat(primas.stream().filter(p -> !p.calculada()).count()).isEqualTo(1);
    }
}
