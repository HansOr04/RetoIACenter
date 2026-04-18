package com.sofka.cotizador.infrastructure.persistence;

import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.ubicacion.AlertaBloqueante;
import com.sofka.cotizador.domain.model.ubicacion.EstadoValidacionUbicacion;
import com.sofka.cotizador.domain.model.ubicacion.Giro;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;
import com.sofka.cotizador.domain.model.ubicacion.ZonaCatastrofica;
import com.sofka.cotizador.domain.port.CotizacionRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

        return new Cotizacion(
                entity.getNumeroFolio(),
                ubicaciones,
                entity.getVersion(),
                entity.getFechaUltimaActualizacion()
        );
    }

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

        EstadoValidacionUbicacion estado = json.getEstadoValidacion() != null
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
                .estadoValidacion(estado)
                .alertasBloqueantes(alertas)
                .build();
    }
}
