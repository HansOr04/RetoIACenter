package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.exception.VersionConflictException;
import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.port.CotizacionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigurarOpcionesCoberturaUseCase {

    private final CotizacionRepository cotizacionRepository;

    public ConfigurarOpcionesCoberturaUseCase(CotizacionRepository cotizacionRepository) {
        this.cotizacionRepository = cotizacionRepository;
    }

    public ConfigurarOpcionesCoberturaResult ejecutar(ConfigurarOpcionesCoberturaCommand command) {
        Cotizacion cotizacion = cotizacionRepository.findByNumeroFolio(command.numeroFolio())
                .orElseThrow(() -> new FolioNotFoundException(command.numeroFolio()));

        if (cotizacion.getVersion() != command.versionEsperada()) {
            throw new VersionConflictException(
                    command.numeroFolio(), cotizacion.getVersion(), command.versionEsperada());
        }

        List<String> warnings = new ArrayList<>();
        if (command.opciones().catTev()) {
            boolean sinZonaTev = cotizacion.getUbicaciones().stream()
                    .allMatch(u -> u.getZonaCatastrofica() == null
                            || u.getZonaCatastrofica().zonaTev() == null);
            if (sinZonaTev) {
                warnings.add("catTev activado pero ninguna ubicación tiene zona TEV configurada");
            }
        }

        Cotizacion actualizada = cotizacion.actualizarOpcionesCobertura(command.opciones());
        cotizacionRepository.save(actualizada);
        return new ConfigurarOpcionesCoberturaResult(actualizada, warnings);
    }
}
