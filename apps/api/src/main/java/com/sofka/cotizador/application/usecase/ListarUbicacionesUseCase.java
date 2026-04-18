package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;
import com.sofka.cotizador.domain.port.CotizacionRepository;
import com.sofka.cotizador.domain.port.FolioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ListarUbicacionesUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListarUbicacionesUseCase.class);

    private final FolioRepository folioRepository;
    private final CotizacionRepository cotizacionRepository;

    public ListarUbicacionesUseCase(FolioRepository folioRepository,
                                    CotizacionRepository cotizacionRepository) {
        this.folioRepository = folioRepository;
        this.cotizacionRepository = cotizacionRepository;
    }

    public List<Ubicacion> ejecutar(String numeroFolio) {
        log.info("Listando ubicaciones de folio numeroFolio={}", numeroFolio);

        folioRepository.findByNumeroFolio(numeroFolio)
                .orElseThrow(() -> new FolioNotFoundException(numeroFolio));

        return cotizacionRepository.findByNumeroFolio(numeroFolio)
                .map(Cotizacion::getUbicaciones)
                .orElse(Collections.emptyList());
    }
}
