package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.exception.LayoutCapacityExceededException;
import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.LayoutUbicaciones;
import com.sofka.cotizador.domain.model.ubicacion.Giro;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;
import com.sofka.cotizador.domain.model.ubicacion.ZonaCatastrofica;
import com.sofka.cotizador.domain.port.CotizacionRepository;
import com.sofka.cotizador.domain.port.FolioRepository;
import com.sofka.cotizador.domain.port.ValidadorCodigoPostalService;
import com.sofka.cotizador.domain.service.ValidadorUbicacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RegistrarUbicacionUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegistrarUbicacionUseCase.class);

    private final FolioRepository folioRepository;
    private final CotizacionRepository cotizacionRepository;
    private final ValidadorCodigoPostalService validadorCodigoPostalService;
    private final ValidadorUbicacion validadorUbicacion;

    public RegistrarUbicacionUseCase(FolioRepository folioRepository,
                                     CotizacionRepository cotizacionRepository,
                                     ValidadorCodigoPostalService validadorCodigoPostalService,
                                     ValidadorUbicacion validadorUbicacion) {
        this.folioRepository = folioRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.validadorCodigoPostalService = validadorCodigoPostalService;
        this.validadorUbicacion = validadorUbicacion;
    }

    public Cotizacion ejecutar(RegistrarUbicacionCommand command) {
        log.info("Registrando ubicación en folio numeroFolio={}", command.numeroFolio());

        var folio = folioRepository.findByNumeroFolio(command.numeroFolio())
                .orElseThrow(() -> new FolioNotFoundException(command.numeroFolio()));

        LayoutUbicaciones layout = folio.getLayoutUbicaciones();
        if (layout == null) {
            throw new IllegalStateException("El folio " + command.numeroFolio()
                    + " no tiene layout de ubicaciones configurado");
        }

        Cotizacion cotizacion = cotizacionRepository.findByNumeroFolio(command.numeroFolio())
                .orElseGet(() -> new Cotizacion(
                        command.numeroFolio(),
                        java.util.Collections.emptyList(),
                        1,
                        LocalDateTime.now()
                ));

        if (cotizacion.capacidadExcedida(layout.getNumeroUbicaciones())) {
            throw new LayoutCapacityExceededException(command.numeroFolio(), layout.getNumeroUbicaciones());
        }

        Optional<ZonaCatastrofica> zonaOpt =
                validadorCodigoPostalService.validarCodigoPostal(command.codigoPostal());

        int nuevoIndice = cotizacion.getUbicaciones().size();

        Giro giro = command.giro() != null
                ? new Giro(command.giro().codigo(), command.giro().descripcion(), command.giro().claveIncendio())
                : null;

        Ubicacion ubicacionPrevia = Ubicacion.builder()
                .indice(nuevoIndice)
                .nombreUbicacion(command.nombreUbicacion())
                .direccion(command.direccion())
                .codigoPostal(command.codigoPostal())
                .estado(command.estado())
                .municipio(command.municipio())
                .colonia(command.colonia())
                .ciudad(command.ciudad())
                .tipoConstructivo(command.tipoConstructivo())
                .nivel(command.nivel())
                .anioConstruccion(command.anioConstruccion())
                .giro(giro)
                .garantias(command.garantias())
                .zonaCatastrofica(zonaOpt.orElse(null))
                .build();

        ValidadorUbicacion.ResultadoValidacion resultado = validadorUbicacion.validar(ubicacionPrevia);

        Ubicacion ubicacionValidada = ubicacionPrevia.toBuilder()
                .alertasBloqueantes(resultado.alertas())
                .estadoValidacion(resultado.estado())
                .build();

        Cotizacion cotizacionActualizada = cotizacion.agregarUbicacion(ubicacionValidada);
        Cotizacion guardada = cotizacionRepository.save(cotizacionActualizada);

        log.info("Ubicación registrada en folio numeroFolio={} ubicacionIndice={}",
                command.numeroFolio(), nuevoIndice);

        return guardada;
    }
}
