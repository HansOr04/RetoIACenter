package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.exception.UbicacionNotFoundException;
import com.sofka.cotizador.domain.exception.VersionConflictException;
import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.ubicacion.Giro;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;
import com.sofka.cotizador.domain.model.ubicacion.ZonaCatastrofica;
import com.sofka.cotizador.domain.port.CotizacionRepository;
import com.sofka.cotizador.domain.port.ValidadorCodigoPostalService;
import com.sofka.cotizador.domain.service.ValidadorUbicacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EditarUbicacionPuntualUseCase {

    private static final Logger log = LoggerFactory.getLogger(EditarUbicacionPuntualUseCase.class);

    private final CotizacionRepository cotizacionRepository;
    private final ValidadorCodigoPostalService validadorCodigoPostalService;
    private final ValidadorUbicacion validadorUbicacion;

    public EditarUbicacionPuntualUseCase(CotizacionRepository cotizacionRepository,
                                         ValidadorCodigoPostalService validadorCodigoPostalService,
                                         ValidadorUbicacion validadorUbicacion) {
        this.cotizacionRepository = cotizacionRepository;
        this.validadorCodigoPostalService = validadorCodigoPostalService;
        this.validadorUbicacion = validadorUbicacion;
    }

    public Cotizacion ejecutar(EditarUbicacionCommand command) {
        log.info("Editando ubicación folio numeroFolio={} ubicacionIndice={}", command.numeroFolio(), command.indice());

        Cotizacion cotizacion = cotizacionRepository.findByNumeroFolio(command.numeroFolio())
                .orElseThrow(() -> new FolioNotFoundException(command.numeroFolio()));

        if (cotizacion.getVersion() != command.versionEsperada()) {
            throw new VersionConflictException(
                    command.numeroFolio(),
                    cotizacion.getVersion(),
                    command.versionEsperada()
            );
        }

        boolean indiceExiste = cotizacion.getUbicaciones().stream()
                .anyMatch(u -> u.getIndice() == command.indice());
        if (!indiceExiste) {
            throw new UbicacionNotFoundException(command.numeroFolio(), command.indice());
        }

        Cotizacion cotizacionActualizada = cotizacion.editarUbicacion(command.indice(), original -> {
            Ubicacion.Builder builder = original.toBuilder();

            if (command.nombreUbicacion() != null) builder.nombreUbicacion(command.nombreUbicacion());
            if (command.direccion() != null) builder.direccion(command.direccion());
            if (command.tipoConstructivo() != null) builder.tipoConstructivo(command.tipoConstructivo());
            if (command.nivel() != null) builder.nivel(command.nivel());
            if (command.anioConstruccion() != null) builder.anioConstruccion(command.anioConstruccion());
            if (command.garantias() != null) builder.garantias(command.garantias());

            if (command.giro() != null) {
                builder.giro(new Giro(
                        command.giro().codigo(),
                        command.giro().descripcion(),
                        command.giro().claveIncendio()
                ));
            }

            ZonaCatastrofica zona = original.getZonaCatastrofica();
            if (command.codigoPostal() != null
                    && !command.codigoPostal().equals(original.getCodigoPostal())) {
                builder.codigoPostal(command.codigoPostal());
                Optional<ZonaCatastrofica> zonaOpt =
                        validadorCodigoPostalService.validarCodigoPostal(command.codigoPostal());
                zona = zonaOpt.orElse(null);
                builder.zonaCatastrofica(zona);
            }

            Ubicacion parcial = builder.build();
            ValidadorUbicacion.ResultadoValidacion resultado = validadorUbicacion.validar(parcial);

            return parcial.toBuilder()
                    .alertasBloqueantes(resultado.alertas())
                    .estadoValidacion(resultado.estado())
                    .build();
        });

        Cotizacion guardada = cotizacionRepository.save(cotizacionActualizada);
        log.info("Ubicación editada folio numeroFolio={} ubicacionIndice={} nuevaVersion={}",
                command.numeroFolio(), command.indice(), guardada.getVersion());
        return guardada;
    }
}
