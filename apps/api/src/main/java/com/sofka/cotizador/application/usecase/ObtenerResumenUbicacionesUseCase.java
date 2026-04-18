package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.ubicacion.EstadoValidacionUbicacion;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;
import com.sofka.cotizador.domain.port.CotizacionRepository;
import com.sofka.cotizador.domain.port.FolioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ObtenerResumenUbicacionesUseCase {

    private static final Logger log = LoggerFactory.getLogger(ObtenerResumenUbicacionesUseCase.class);

    private final FolioRepository folioRepository;
    private final CotizacionRepository cotizacionRepository;

    public ObtenerResumenUbicacionesUseCase(FolioRepository folioRepository,
                                            CotizacionRepository cotizacionRepository) {
        this.folioRepository = folioRepository;
        this.cotizacionRepository = cotizacionRepository;
    }

    public ResumenUbicaciones ejecutar(String numeroFolio) {
        log.info("Obteniendo resumen de ubicaciones para folio numeroFolio={}", numeroFolio);

        folioRepository.findByNumeroFolio(numeroFolio)
                .orElseThrow(() -> new FolioNotFoundException(numeroFolio));

        List<Ubicacion> ubicaciones = cotizacionRepository.findByNumeroFolio(numeroFolio)
                .map(Cotizacion::getUbicaciones)
                .orElse(Collections.emptyList());

        int total = ubicaciones.size();
        int completas = (int) ubicaciones.stream()
                .filter(u -> u.getEstadoValidacion() == EstadoValidacionUbicacion.VALIDO)
                .count();
        int incompletas = total - completas;
        int calculables = (int) ubicaciones.stream()
                .filter(Ubicacion::esCalculable)
                .count();

        List<Integer> indicesIncompletos = ubicaciones.stream()
                .filter(u -> u.getEstadoValidacion() != EstadoValidacionUbicacion.VALIDO)
                .map(Ubicacion::getIndice)
                .toList();

        List<DetalleIncompleta> detalles = ubicaciones.stream()
                .filter(u -> u.getEstadoValidacion() != EstadoValidacionUbicacion.VALIDO)
                .map(u -> new DetalleIncompleta(u.getIndice(), u.getNombreUbicacion(), u.getAlertasBloqueantes()))
                .toList();

        return new ResumenUbicaciones(total, completas, incompletas, calculables, indicesIncompletos, detalles);
    }

    public record ResumenUbicaciones(
            int total,
            int completas,
            int incompletas,
            int calculables,
            List<Integer> indicesIncompletos,
            List<DetalleIncompleta> detalleIncompletas
    ) {}

    public record DetalleIncompleta(
            int indice,
            String nombreUbicacion,
            List<com.sofka.cotizador.domain.model.ubicacion.AlertaBloqueante> alertas
    ) {}
}
