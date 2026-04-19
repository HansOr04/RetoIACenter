package com.sofka.cotizador.domain.service;

import com.sofka.cotizador.domain.model.OpcionesCobertura;
import com.sofka.cotizador.domain.model.calculo.ParametrosCalculo;
import com.sofka.cotizador.domain.model.calculo.PrimaPorUbicacion;
import com.sofka.cotizador.domain.model.ubicacion.AlertaBloqueante;
import com.sofka.cotizador.domain.model.ubicacion.Giro;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;
import com.sofka.cotizador.domain.model.ubicacion.ZonaCatastrofica;
import com.sofka.cotizador.domain.port.CatalogoTarifasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculoPrimaServiceTest {

    @Mock
    private CatalogoTarifasRepository catalogo;

    private CalculoPrimaService service;

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
        service = new CalculoPrimaService();
    }

    private Ubicacion ubicacionValida() {
        return Ubicacion.builder()
                .indice(0)
                .tipoConstructivo("CONCRETO_ARMADO")
                .nivel(1)
                .giro(new Giro("B1", "Oficinas", "B1"))
                .zonaCatastrofica(new ZonaCatastrofica("TEV-B", "FHM-2"))
                .build();
    }

    private Ubicacion ubicacionConAlerta() {
        return Ubicacion.builder()
                .indice(1)
                .alertasBloqueantes(List.of(new AlertaBloqueante("CP_INVALIDO", "CP no válido", "codigoPostal")))
                .build();
    }

    @Test
    void calcularUbicacion_ubicacionIncalculable_retornaPrimaPorUbicacionFalse() {
        PrimaPorUbicacion resultado = service.calcularUbicacion(
                ubicacionConAlerta(), OpcionesCobertura.defaults(), catalogo);

        assertThat(resultado.calculada()).isFalse();
        assertThat(resultado.total()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resultado.alertas()).hasSize(1);
    }

    @Test
    void calcularUbicacion_soloIncendioEdificios_calculaCorrectamente() {
        when(catalogo.findParametrosCalculo()).thenReturn(PARAMS);
        when(catalogo.findTarifaIncendio("B1", "CONCRETO_ARMADO"))
                .thenReturn(Optional.of(new CatalogoTarifasRepository.TarifaIncendio(
                        new BigDecimal("0.0020"), new BigDecimal("0.0030"))));

        PrimaPorUbicacion resultado = service.calcularUbicacion(
                ubicacionValida(), OpcionesCobertura.defaults(), catalogo);

        assertThat(resultado.calculada()).isTrue();
        // 1_000_000 * 0.0020 = 2000.00
        assertThat(resultado.desglose().incendioEdificios()).isEqualByComparingTo("2000.00");
        assertThat(resultado.desglose().incendioContenidos()).isEqualByComparingTo("0");
        assertThat(resultado.total()).isEqualByComparingTo("2000.00");
    }

    @Test
    void calcularUbicacion_incendioEdificiosYContenidos_sumaAmbos() {
        when(catalogo.findParametrosCalculo()).thenReturn(PARAMS);
        when(catalogo.findTarifaIncendio("B1", "CONCRETO_ARMADO"))
                .thenReturn(Optional.of(new CatalogoTarifasRepository.TarifaIncendio(
                        new BigDecimal("0.0020"), new BigDecimal("0.0030"))));

        OpcionesCobertura coberturas = new OpcionesCobertura(
                true, true, false, false, false, false,
                false, false, false, false, false, false, false, false);

        PrimaPorUbicacion resultado = service.calcularUbicacion(ubicacionValida(), coberturas, catalogo);

        assertThat(resultado.desglose().incendioEdificios()).isEqualByComparingTo("2000.00");
        assertThat(resultado.desglose().incendioContenidos()).isEqualByComparingTo("3000.00");
        assertThat(resultado.total()).isEqualByComparingTo("5000.00");
    }

    @Test
    void calcularUbicacion_extensionCobertura_calculaSobreIncendios() {
        when(catalogo.findParametrosCalculo()).thenReturn(PARAMS);
        when(catalogo.findTarifaIncendio("B1", "CONCRETO_ARMADO"))
                .thenReturn(Optional.of(new CatalogoTarifasRepository.TarifaIncendio(
                        new BigDecimal("0.0020"), new BigDecimal("0.0030"))));

        OpcionesCobertura coberturas = new OpcionesCobertura(
                true, true, true, false, false, false,
                false, false, false, false, false, false, false, false);

        PrimaPorUbicacion resultado = service.calcularUbicacion(ubicacionValida(), coberturas, catalogo);

        // ext = (2000 + 3000) * 0.002 = 10.00
        assertThat(resultado.desglose().extensionCobertura()).isEqualByComparingTo("10.00");
        assertThat(resultado.total()).isEqualByComparingTo("5010.00");
    }

    @Test
    void calcularUbicacion_catTev_calculaSobreZonaTev() {
        when(catalogo.findParametrosCalculo()).thenReturn(PARAMS);
        when(catalogo.findTarifaIncendio(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(catalogo.findTasaCatTev("TEV-B"))
                .thenReturn(Optional.of(new BigDecimal("0.0015")));

        OpcionesCobertura coberturas = new OpcionesCobertura(
                true, false, false, true, false, false,
                false, false, false, false, false, false, false, false);

        PrimaPorUbicacion resultado = service.calcularUbicacion(ubicacionValida(), coberturas, catalogo);

        // catTev = 1_000_000 * 0.0015 = 1500.00
        assertThat(resultado.desglose().catTev()).isEqualByComparingTo("1500.00");
    }

    @Test
    void calcularUbicacion_catTevSinZona_retornaCero() {
        when(catalogo.findParametrosCalculo()).thenReturn(PARAMS);

        Ubicacion sinZona = Ubicacion.builder()
                .indice(0)
                .tipoConstructivo("CONCRETO_ARMADO")
                .giro(new Giro("B1", "Oficinas", "B1"))
                .build();

        OpcionesCobertura coberturas = new OpcionesCobertura(
                true, false, false, true, false, false,
                false, false, false, false, false, false, false, false);

        when(catalogo.findTarifaIncendio(anyString(), anyString()))
                .thenReturn(Optional.empty());

        PrimaPorUbicacion resultado = service.calcularUbicacion(sinZona, coberturas, catalogo);

        assertThat(resultado.desglose().catTev()).isEqualByComparingTo("0");
    }

    @Test
    void calcularUbicacion_catFhm_calculaSobreZonaFhm() {
        when(catalogo.findParametrosCalculo()).thenReturn(PARAMS);
        when(catalogo.findTarifaIncendio(anyString(), anyString())).thenReturn(Optional.empty());
        when(catalogo.findTasaCatFhm("FHM-2")).thenReturn(Optional.of(new BigDecimal("0.0010")));

        OpcionesCobertura coberturas = new OpcionesCobertura(
                true, false, false, false, true, false,
                false, false, false, false, false, false, false, false);

        PrimaPorUbicacion resultado = service.calcularUbicacion(ubicacionValida(), coberturas, catalogo);

        // catFhm = 1_000_000 * 0.0010 = 1000.00
        assertThat(resultado.desglose().catFhm()).isEqualByComparingTo("1000.00");
    }

    @Test
    void calcularUbicacion_equipoElectronico_usaFactorDeRepo() {
        when(catalogo.findParametrosCalculo()).thenReturn(PARAMS);
        when(catalogo.findTarifaIncendio(anyString(), anyString())).thenReturn(Optional.empty());
        when(catalogo.findFactorEquipoElectronico("B", 1))
                .thenReturn(Optional.of(new BigDecimal("0.0045")));

        OpcionesCobertura coberturas = new OpcionesCobertura(
                true, false, false, false, false, false,
                false, false, false, true, false, false, false, false);

        PrimaPorUbicacion resultado = service.calcularUbicacion(ubicacionValida(), coberturas, catalogo);

        // equipo = 1_000_000 * 0.0045 = 4500.00
        assertThat(resultado.desglose().equipoElectronico()).isEqualByComparingTo("4500.00");
    }

    @Test
    void calcularUbicacion_equipoElectronico_usaDefaultCuandoNoHayFactor() {
        when(catalogo.findParametrosCalculo()).thenReturn(PARAMS);
        when(catalogo.findTarifaIncendio(anyString(), anyString())).thenReturn(Optional.empty());
        when(catalogo.findFactorEquipoElectronico(anyString(), anyInt())).thenReturn(Optional.empty());

        OpcionesCobertura coberturas = new OpcionesCobertura(
                true, false, false, false, false, false,
                false, false, false, true, false, false, false, false);

        PrimaPorUbicacion resultado = service.calcularUbicacion(ubicacionValida(), coberturas, catalogo);

        // fallback = 1_000_000 * 0.0045 = 4500.00
        assertThat(resultado.desglose().equipoElectronico()).isEqualByComparingTo("4500.00");
    }

    @Test
    void calcularUbicacion_robo_calculaSobreIncendioContenidos() {
        when(catalogo.findParametrosCalculo()).thenReturn(PARAMS);
        when(catalogo.findTarifaIncendio("B1", "CONCRETO_ARMADO"))
                .thenReturn(Optional.of(new CatalogoTarifasRepository.TarifaIncendio(
                        new BigDecimal("0.0020"), new BigDecimal("0.0030"))));

        OpcionesCobertura coberturas = new OpcionesCobertura(
                false, true, false, false, false, false,
                false, false, false, false, true, false, false, false);

        PrimaPorUbicacion resultado = service.calcularUbicacion(ubicacionValida(), coberturas, catalogo);

        // robo = 3000 * 0.15 = 450.00
        assertThat(resultado.desglose().robo()).isEqualByComparingTo("450.00");
    }

    @Test
    void calcularUbicacion_remocionEscombros_calculaSobreIncendioEdificios() {
        when(catalogo.findParametrosCalculo()).thenReturn(PARAMS);
        when(catalogo.findTarifaIncendio("B1", "CONCRETO_ARMADO"))
                .thenReturn(Optional.of(new CatalogoTarifasRepository.TarifaIncendio(
                        new BigDecimal("0.0020"), new BigDecimal("0.0030"))));

        OpcionesCobertura coberturas = new OpcionesCobertura(
                true, false, false, false, false, true,
                false, false, false, false, false, false, false, false);

        PrimaPorUbicacion resultado = service.calcularUbicacion(ubicacionValida(), coberturas, catalogo);

        // remocion = 2000 * 0.001 = 2.00
        assertThat(resultado.desglose().remocionEscombros()).isEqualByComparingTo("2.00");
    }

    @Test
    void calcularUbicacion_ejemploDocumentado_coincideConEsperado() {
        // B1 + CONCRETO_ARMADO + incEdif + incCont + catTev(TEV-B) + extension
        when(catalogo.findParametrosCalculo()).thenReturn(PARAMS);
        when(catalogo.findTarifaIncendio("B1", "CONCRETO_ARMADO"))
                .thenReturn(Optional.of(new CatalogoTarifasRepository.TarifaIncendio(
                        new BigDecimal("0.0020"), new BigDecimal("0.0030"))));
        when(catalogo.findTasaCatTev("TEV-B")).thenReturn(Optional.of(new BigDecimal("0.0015")));

        OpcionesCobertura coberturas = new OpcionesCobertura(
                true, true, true, true, false, false,
                false, false, false, false, false, false, false, false);

        PrimaPorUbicacion resultado = service.calcularUbicacion(ubicacionValida(), coberturas, catalogo);

        // incEdif=2000, incCont=3000, ext=(5000*0.002)=10, catTev=1500, total=6510
        assertThat(resultado.desglose().incendioEdificios()).isEqualByComparingTo("2000.00");
        assertThat(resultado.desglose().incendioContenidos()).isEqualByComparingTo("3000.00");
        assertThat(resultado.desglose().extensionCobertura()).isEqualByComparingTo("10.00");
        assertThat(resultado.desglose().catTev()).isEqualByComparingTo("1500.00");
        assertThat(resultado.total()).isEqualByComparingTo("6510.00");
    }
}
