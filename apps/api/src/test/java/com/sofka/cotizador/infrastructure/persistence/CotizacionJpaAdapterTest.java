package com.sofka.cotizador.infrastructure.persistence;

import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.EstadoCotizacion;
import com.sofka.cotizador.domain.model.OpcionesCobertura;
import com.sofka.cotizador.domain.model.calculo.DesgloseComponentes;
import com.sofka.cotizador.domain.model.calculo.PrimaPorUbicacion;
import com.sofka.cotizador.domain.model.calculo.ResultadoCalculo;
import com.sofka.cotizador.domain.model.ubicacion.AlertaBloqueante;
import com.sofka.cotizador.domain.model.ubicacion.EstadoValidacionUbicacion;
import com.sofka.cotizador.domain.model.ubicacion.Giro;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;
import com.sofka.cotizador.domain.model.ubicacion.ZonaCatastrofica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CotizacionJpaAdapterTest {

    @Mock
    private CotizacionJpaRepository jpaRepository;

    private CotizacionJpaAdapter adapter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        adapter = new CotizacionJpaAdapter(jpaRepository);
    }

    @Test
    void testSaveCotizacion() {
        Cotizacion cotizacion = createFullCotizacionDomain();
        CotizacionJpaEntity entityToReturn = createFullCotizacionEntity();
        
        when(jpaRepository.save(any(CotizacionJpaEntity.class))).thenReturn(entityToReturn);

        Cotizacion result = adapter.save(cotizacion);

        assertNotNull(result);
        assertEquals("F-TEST01", result.getNumeroFolio());
        assertEquals(1, result.getVersion());
        assertEquals(EstadoCotizacion.CALCULADO, result.getEstado());
        assertNotNull(result.getOpcionesCobertura());
        assertNotNull(result.getResultadoCalculo());
        assertEquals(1, result.getUbicaciones().size());
        assertEquals("G-001", result.getUbicaciones().get(0).getGiro().codigo());
        
        verify(jpaRepository).save(any(CotizacionJpaEntity.class));
    }

    @Test
    void testFindByNumeroFolio() {
        CotizacionJpaEntity entityToReturn = createFullCotizacionEntity();
        when(jpaRepository.findById("F-TEST01")).thenReturn(Optional.of(entityToReturn));

        Optional<Cotizacion> resultOpt = adapter.findByNumeroFolio("F-TEST01");

        assertTrue(resultOpt.isPresent());
        Cotizacion result = resultOpt.get();
        assertEquals("F-TEST01", result.getNumeroFolio());
        assertEquals(1, result.getVersion());
        assertEquals(EstadoCotizacion.CALCULADO, result.getEstado());
        assertNotNull(result.getOpcionesCobertura());
        assertNotNull(result.getResultadoCalculo());
        assertEquals(1, result.getUbicaciones().size());
        assertEquals("G-001", result.getUbicaciones().get(0).getGiro().codigo());
        assertEquals("TEV-A", result.getUbicaciones().get(0).getZonaCatastrofica().zonaTev());
    }

    private Cotizacion createFullCotizacionDomain() {
        Ubicacion ubicacion = Ubicacion.builder()
                .indice(0)
                .nombreUbicacion("Ubicacion 1")
                .direccion("Direccion 1")
                .codigoPostal("12345")
                .estado("Estado 1")
                .municipio("Municipio 1")
                .colonia("Colonia 1")
                .ciudad("Ciudad 1")
                .tipoConstructivo("MAMPOSTERIA")
                .nivel(1)
                .anioConstruccion(2000)
                .giro(new Giro("G-001", "Giro 1", "INC-01"))
                .garantias(List.of("Incendio"))
                .zonaCatastrofica(new ZonaCatastrofica("TEV-A", "FHM-1"))
                .estadoValidacion(EstadoValidacionUbicacion.VALIDO)
                .alertasBloqueantes(List.of(new AlertaBloqueante("A-01", "Alerta", "Campo")))
                .build();

        OpcionesCobertura opciones = new OpcionesCobertura(
                true, true, true, true, true, true, true, true, true, true, true, true, true, true
        );

        DesgloseComponentes desglose = new DesgloseComponentes(
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN
        );

        PrimaPorUbicacion prima = new PrimaPorUbicacion(0, true, BigDecimal.TEN, desglose, List.of(new AlertaBloqueante("A-01", "Alerta", "Campo")));

        ResultadoCalculo calculo = new ResultadoCalculo(
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ONE,
                List.of(prima), LocalDateTime.now()
        );

        return new Cotizacion("F-TEST01", List.of(ubicacion), 1, LocalDateTime.now(), opciones, calculo, EstadoCotizacion.CALCULADO);
    }

    private CotizacionJpaEntity createFullCotizacionEntity() {
        DatosCotizacion.AlertaBloqueanteDatos alertaDatos = new DatosCotizacion.AlertaBloqueanteDatos();
        alertaDatos.setCodigo("A-01");
        alertaDatos.setMensaje("Alerta");
        alertaDatos.setCampoAfectado("Campo");

        DatosCotizacion.DesgloseComponentesDatos desgloseDatos = new DatosCotizacion.DesgloseComponentesDatos();
        desgloseDatos.setIncendioEdificios(BigDecimal.ONE);
        desgloseDatos.setIncendioContenidos(BigDecimal.ONE);
        desgloseDatos.setExtensionCobertura(BigDecimal.ONE);
        desgloseDatos.setCatTev(BigDecimal.ONE);
        desgloseDatos.setCatFhm(BigDecimal.ONE);
        desgloseDatos.setRemocionEscombros(BigDecimal.ONE);
        desgloseDatos.setGastosExtraordinarios(BigDecimal.ONE);
        desgloseDatos.setPerdidaRentas(BigDecimal.ONE);
        desgloseDatos.setBi(BigDecimal.ONE);
        desgloseDatos.setEquipoElectronico(BigDecimal.ONE);
        desgloseDatos.setRobo(BigDecimal.ONE);
        desgloseDatos.setDineroValores(BigDecimal.ONE);
        desgloseDatos.setVidrios(BigDecimal.ONE);
        desgloseDatos.setAnunciosLuminosos(BigDecimal.ONE);
        desgloseDatos.setTotal(BigDecimal.TEN);

        DatosCotizacion.PrimaPorUbicacionDatos primaDatos = new DatosCotizacion.PrimaPorUbicacionDatos();
        primaDatos.setIndice(0);
        primaDatos.setCalculada(true);
        primaDatos.setTotal(BigDecimal.TEN);
        primaDatos.setDesglose(desgloseDatos);
        primaDatos.setAlertas(List.of(alertaDatos));

        DatosCotizacion.ResultadoCalculoDatos calculoDatos = new DatosCotizacion.ResultadoCalculoDatos();
        calculoDatos.setPrimaNeta(BigDecimal.TEN);
        calculoDatos.setPrimaComercial(BigDecimal.TEN);
        calculoDatos.setFactorComercial(BigDecimal.ONE);
        calculoDatos.setFechaCalculo(LocalDateTime.now());
        calculoDatos.setPrimasPorUbicacion(List.of(primaDatos));

        DatosCotizacion.OpcionesCoberturaDatos opcionesDatos = new DatosCotizacion.OpcionesCoberturaDatos();
        opcionesDatos.setIncendioEdificios(true);
        opcionesDatos.setIncendioContenidos(true);
        opcionesDatos.setExtensionCobertura(true);
        opcionesDatos.setCatTev(true);
        opcionesDatos.setCatFhm(true);
        opcionesDatos.setRemocionEscombros(true);
        opcionesDatos.setGastosExtraordinarios(true);
        opcionesDatos.setPerdidaRentas(true);
        opcionesDatos.setBi(true);
        opcionesDatos.setEquipoElectronico(true);
        opcionesDatos.setRobo(true);
        opcionesDatos.setDineroValores(true);
        opcionesDatos.setVidrios(true);
        opcionesDatos.setAnunciosLuminosos(true);

        UbicacionJson.GiroJson giroJson = new UbicacionJson.GiroJson();
        giroJson.setCodigo("G-001");
        giroJson.setDescripcion("Giro 1");
        giroJson.setClaveIncendio("INC-01");

        UbicacionJson.ZonaCatastroficaJson zonaJson = new UbicacionJson.ZonaCatastroficaJson();
        zonaJson.setZonaTev("TEV-A");
        zonaJson.setZonaFhm("FHM-1");

        UbicacionJson.AlertaBloquenanteJson alertaJson = new UbicacionJson.AlertaBloquenanteJson();
        alertaJson.setCodigo("A-01");
        alertaJson.setMensaje("Alerta");
        alertaJson.setCampoAfectado("Campo");

        UbicacionJson ubicacionJson = new UbicacionJson();
        ubicacionJson.setIndice(0);
        ubicacionJson.setNombreUbicacion("Ubicacion 1");
        ubicacionJson.setDireccion("Direccion 1");
        ubicacionJson.setCodigoPostal("12345");
        ubicacionJson.setEstado("Estado 1");
        ubicacionJson.setMunicipio("Municipio 1");
        ubicacionJson.setColonia("Colonia 1");
        ubicacionJson.setCiudad("Ciudad 1");
        ubicacionJson.setTipoConstructivo("MAMPOSTERIA");
        ubicacionJson.setNivel(1);
        ubicacionJson.setAnioConstruccion(2000);
        ubicacionJson.setGiro(giroJson);
        ubicacionJson.setGarantias(List.of("Incendio"));
        ubicacionJson.setZonaCatastrofica(zonaJson);
        ubicacionJson.setEstadoValidacion(EstadoValidacionUbicacion.VALIDO.name());
        ubicacionJson.setAlertasBloqueantes(List.of(alertaJson));

        DatosCotizacion datos = new DatosCotizacion();
        datos.setUbicaciones(List.of(ubicacionJson));
        datos.setOpcionesCobertura(opcionesDatos);
        datos.setResultadoCalculo(calculoDatos);
        datos.setEstado(EstadoCotizacion.CALCULADO.name());

        return CotizacionJpaEntity.builder()
                .numeroFolio("F-TEST01")
                .version(1)
                .fechaUltimaActualizacion(LocalDateTime.now())
                .datos(datos)
                .build();
    }
}
