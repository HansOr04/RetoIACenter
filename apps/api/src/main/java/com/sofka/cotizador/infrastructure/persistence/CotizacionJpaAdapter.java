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
import com.sofka.cotizador.domain.port.CotizacionRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CotizacionJpaAdapter implements CotizacionRepository {

    private final CotizacionJpaRepository jpaRepository;

    public CotizacionJpaAdapter(CotizacionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Cotizacion> findByNumeroFolio(String numeroFolio) {
        return jpaRepository.findById(numeroFolio).map(this::toDomain);
    }

    @Override
    public Cotizacion save(Cotizacion cotizacion) {
        CotizacionJpaEntity entity = toEntity(cotizacion);
        CotizacionJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private CotizacionJpaEntity toEntity(Cotizacion cotizacion) {
        DatosCotizacion datos = new DatosCotizacion();
        datos.setUbicaciones(
                cotizacion.getUbicaciones().stream()
                        .map(this::toUbicacionJson)
                        .toList()
        );

        if (cotizacion.getOpcionesCobertura() != null) {
            datos.setOpcionesCobertura(toOpcionesCoberturaDatos(cotizacion.getOpcionesCobertura()));
        }

        if (cotizacion.getResultadoCalculo() != null) {
            datos.setResultadoCalculo(toResultadoCalculoDatos(cotizacion.getResultadoCalculo()));
        }

        if (cotizacion.getEstado() != null) {
            datos.setEstado(cotizacion.getEstado().name());
        }

        return CotizacionJpaEntity.builder()
                .numeroFolio(cotizacion.getNumeroFolio())
                .version(cotizacion.getVersion())
                .fechaUltimaActualizacion(cotizacion.getFechaUltimaActualizacion())
                .datos(datos)
                .build();
    }

    private Cotizacion toDomain(CotizacionJpaEntity entity) {
        List<Ubicacion> ubicaciones = entity.getDatos() != null
                && entity.getDatos().getUbicaciones() != null
                ? entity.getDatos().getUbicaciones().stream()
                        .map(this::toUbicacionDomain)
                        .toList()
                : new ArrayList<>();

        OpcionesCobertura opcionesCobertura = null;
        ResultadoCalculo resultadoCalculo = null;
        EstadoCotizacion estado = null;

        if (entity.getDatos() != null) {
            if (entity.getDatos().getOpcionesCobertura() != null) {
                opcionesCobertura = toOpcionesCoberturaDomain(entity.getDatos().getOpcionesCobertura());
            }
            if (entity.getDatos().getResultadoCalculo() != null) {
                resultadoCalculo = toResultadoCalculoDomain(entity.getDatos().getResultadoCalculo());
            }
            if (entity.getDatos().getEstado() != null) {
                estado = EstadoCotizacion.valueOf(entity.getDatos().getEstado());
            }
        }

        return new Cotizacion(
                entity.getNumeroFolio(),
                ubicaciones,
                entity.getVersion(),
                entity.getFechaUltimaActualizacion(),
                opcionesCobertura,
                resultadoCalculo,
                estado
        );
    }

    // ── OpcionesCobertura mapping ──────────────────────────────────────────────

    private DatosCotizacion.OpcionesCoberturaDatos toOpcionesCoberturaDatos(OpcionesCobertura o) {
        DatosCotizacion.OpcionesCoberturaDatos d = new DatosCotizacion.OpcionesCoberturaDatos();
        d.setIncendioEdificios(o.incendioEdificios());
        d.setIncendioContenidos(o.incendioContenidos());
        d.setExtensionCobertura(o.extensionCobertura());
        d.setCatTev(o.catTev());
        d.setCatFhm(o.catFhm());
        d.setRemocionEscombros(o.remocionEscombros());
        d.setGastosExtraordinarios(o.gastosExtraordinarios());
        d.setPerdidaRentas(o.perdidaRentas());
        d.setBi(o.bi());
        d.setEquipoElectronico(o.equipoElectronico());
        d.setRobo(o.robo());
        d.setDineroValores(o.dineroValores());
        d.setVidrios(o.vidrios());
        d.setAnunciosLuminosos(o.anunciosLuminosos());
        return d;
    }

    private OpcionesCobertura toOpcionesCoberturaDomain(DatosCotizacion.OpcionesCoberturaDatos d) {
        return new OpcionesCobertura(
                d.isIncendioEdificios(), d.isIncendioContenidos(), d.isExtensionCobertura(),
                d.isCatTev(), d.isCatFhm(), d.isRemocionEscombros(), d.isGastosExtraordinarios(),
                d.isPerdidaRentas(), d.isBi(), d.isEquipoElectronico(), d.isRobo(),
                d.isDineroValores(), d.isVidrios(), d.isAnunciosLuminosos()
        );
    }

    // ── ResultadoCalculo mapping ───────────────────────────────────────────────

    private DatosCotizacion.ResultadoCalculoDatos toResultadoCalculoDatos(ResultadoCalculo r) {
        DatosCotizacion.ResultadoCalculoDatos d = new DatosCotizacion.ResultadoCalculoDatos();
        d.setPrimaNeta(r.primaNeta());
        d.setPrimaComercial(r.primaComercial());
        d.setFactorComercial(r.factorComercial());
        d.setFechaCalculo(r.fechaCalculo());
        if (r.primasPorUbicacion() != null) {
            d.setPrimasPorUbicacion(
                    r.primasPorUbicacion().stream()
                            .map(this::toPrimaPorUbicacionDatos)
                            .toList()
            );
        }
        return d;
    }

    private ResultadoCalculo toResultadoCalculoDomain(DatosCotizacion.ResultadoCalculoDatos d) {
        List<PrimaPorUbicacion> primas = new ArrayList<>();
        if (d.getPrimasPorUbicacion() != null) {
            primas = d.getPrimasPorUbicacion().stream()
                    .map(this::toPrimaPorUbicacionDomain)
                    .toList();
        }
        return new ResultadoCalculo(
                d.getPrimaNeta(), d.getPrimaComercial(), d.getFactorComercial(),
                primas, d.getFechaCalculo()
        );
    }

    private DatosCotizacion.PrimaPorUbicacionDatos toPrimaPorUbicacionDatos(PrimaPorUbicacion p) {
        DatosCotizacion.PrimaPorUbicacionDatos d = new DatosCotizacion.PrimaPorUbicacionDatos();
        d.setIndice(p.indice());
        d.setCalculada(p.calculada());
        d.setTotal(p.total());
        if (p.desglose() != null) {
            d.setDesglose(toDesgloseComponentesDatos(p.desglose()));
        }
        if (p.alertas() != null) {
            d.setAlertas(p.alertas().stream()
                    .map(a -> {
                        DatosCotizacion.AlertaBloqueanteDatos ad = new DatosCotizacion.AlertaBloqueanteDatos();
                        ad.setCodigo(a.codigo());
                        ad.setMensaje(a.mensaje());
                        ad.setCampoAfectado(a.campoAfectado());
                        return ad;
                    })
                    .toList());
        }
        return d;
    }

    private PrimaPorUbicacion toPrimaPorUbicacionDomain(DatosCotizacion.PrimaPorUbicacionDatos d) {
        DesgloseComponentes desglose = null;
        if (d.getDesglose() != null) {
            desglose = toDesgloseComponentesDomain(d.getDesglose());
        }
        List<AlertaBloqueante> alertas = new ArrayList<>();
        if (d.getAlertas() != null) {
            alertas = d.getAlertas().stream()
                    .map(a -> new AlertaBloqueante(a.getCodigo(), a.getMensaje(), a.getCampoAfectado()))
                    .toList();
        }
        return new PrimaPorUbicacion(d.getIndice(), d.isCalculada(), d.getTotal(), desglose, alertas);
    }

    private DatosCotizacion.DesgloseComponentesDatos toDesgloseComponentesDatos(DesgloseComponentes dc) {
        DatosCotizacion.DesgloseComponentesDatos d = new DatosCotizacion.DesgloseComponentesDatos();
        d.setIncendioEdificios(dc.incendioEdificios());
        d.setIncendioContenidos(dc.incendioContenidos());
        d.setExtensionCobertura(dc.extensionCobertura());
        d.setCatTev(dc.catTev());
        d.setCatFhm(dc.catFhm());
        d.setRemocionEscombros(dc.remocionEscombros());
        d.setGastosExtraordinarios(dc.gastosExtraordinarios());
        d.setPerdidaRentas(dc.perdidaRentas());
        d.setBi(dc.bi());
        d.setEquipoElectronico(dc.equipoElectronico());
        d.setRobo(dc.robo());
        d.setDineroValores(dc.dineroValores());
        d.setVidrios(dc.vidrios());
        d.setAnunciosLuminosos(dc.anunciosLuminosos());
        d.setTotal(dc.total());
        return d;
    }

    private DesgloseComponentes toDesgloseComponentesDomain(DatosCotizacion.DesgloseComponentesDatos d) {
        return new DesgloseComponentes(
                d.getIncendioEdificios(), d.getIncendioContenidos(), d.getExtensionCobertura(),
                d.getCatTev(), d.getCatFhm(), d.getRemocionEscombros(), d.getGastosExtraordinarios(),
                d.getPerdidaRentas(), d.getBi(), d.getEquipoElectronico(), d.getRobo(),
                d.getDineroValores(), d.getVidrios(), d.getAnunciosLuminosos(), d.getTotal()
        );
    }

    // ── Ubicacion mapping (unchanged) ─────────────────────────────────────────

    private UbicacionJson toUbicacionJson(Ubicacion u) {
        UbicacionJson json = new UbicacionJson();
        json.setIndice(u.getIndice());
        json.setNombreUbicacion(u.getNombreUbicacion());
        json.setDireccion(u.getDireccion());
        json.setCodigoPostal(u.getCodigoPostal());
        json.setEstado(u.getEstado());
        json.setMunicipio(u.getMunicipio());
        json.setColonia(u.getColonia());
        json.setCiudad(u.getCiudad());
        json.setTipoConstructivo(u.getTipoConstructivo());
        json.setNivel(u.getNivel());
        json.setAnioConstruccion(u.getAnioConstruccion());
        json.setGarantias(u.getGarantias() != null ? new ArrayList<>(u.getGarantias()) : null);
        json.setEstadoValidacion(u.getEstadoValidacion() != null ? u.getEstadoValidacion().name() : null);

        if (u.getGiro() != null) {
            UbicacionJson.GiroJson giroJson = new UbicacionJson.GiroJson();
            giroJson.setCodigo(u.getGiro().codigo());
            giroJson.setDescripcion(u.getGiro().descripcion());
            giroJson.setClaveIncendio(u.getGiro().claveIncendio());
            json.setGiro(giroJson);
        }

        if (u.getZonaCatastrofica() != null) {
            UbicacionJson.ZonaCatastroficaJson zonaJson = new UbicacionJson.ZonaCatastroficaJson();
            zonaJson.setZonaTev(u.getZonaCatastrofica().zonaTev());
            zonaJson.setZonaFhm(u.getZonaCatastrofica().zonaFhm());
            json.setZonaCatastrofica(zonaJson);
        }

        if (u.getAlertasBloqueantes() != null) {
            json.setAlertasBloqueantes(
                    u.getAlertasBloqueantes().stream()
                            .map(a -> {
                                UbicacionJson.AlertaBloquenanteJson aj = new UbicacionJson.AlertaBloquenanteJson();
                                aj.setCodigo(a.codigo());
                                aj.setMensaje(a.mensaje());
                                aj.setCampoAfectado(a.campoAfectado());
                                return aj;
                            })
                            .toList()
            );
        }

        return json;
    }

    private Ubicacion toUbicacionDomain(UbicacionJson json) {
        Giro giro = null;
        if (json.getGiro() != null) {
            giro = new Giro(
                    json.getGiro().getCodigo(),
                    json.getGiro().getDescripcion(),
                    json.getGiro().getClaveIncendio()
            );
        }

        ZonaCatastrofica zona = null;
        if (json.getZonaCatastrofica() != null) {
            zona = new ZonaCatastrofica(
                    json.getZonaCatastrofica().getZonaTev(),
                    json.getZonaCatastrofica().getZonaFhm()
            );
        }

        EstadoValidacionUbicacion estadoUbicacion = json.getEstadoValidacion() != null
                ? EstadoValidacionUbicacion.valueOf(json.getEstadoValidacion())
                : EstadoValidacionUbicacion.INCOMPLETO;

        List<AlertaBloqueante> alertas = new ArrayList<>();
        if (json.getAlertasBloqueantes() != null) {
            alertas = json.getAlertasBloqueantes().stream()
                    .map(a -> new AlertaBloqueante(a.getCodigo(), a.getMensaje(), a.getCampoAfectado()))
                    .toList();
        }

        return Ubicacion.builder()
                .indice(json.getIndice())
                .nombreUbicacion(json.getNombreUbicacion())
                .direccion(json.getDireccion())
                .codigoPostal(json.getCodigoPostal())
                .estado(json.getEstado())
                .municipio(json.getMunicipio())
                .colonia(json.getColonia())
                .ciudad(json.getCiudad())
                .tipoConstructivo(json.getTipoConstructivo())
                .nivel(json.getNivel())
                .anioConstruccion(json.getAnioConstruccion())
                .giro(giro)
                .garantias(json.getGarantias() != null ? new ArrayList<>(json.getGarantias()) : new ArrayList<>())
                .zonaCatastrofica(zona)
                .estadoValidacion(estadoUbicacion)
                .alertasBloqueantes(alertas)
                .build();
    }
}
