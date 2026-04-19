package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.exception.SinUbicacionesCalculablesException;
import com.sofka.cotizador.domain.exception.VersionConflictException;
import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.OpcionesCobertura;
import com.sofka.cotizador.domain.model.calculo.ParametrosCalculo;
import com.sofka.cotizador.domain.model.calculo.PrimaPorUbicacion;
import com.sofka.cotizador.domain.model.calculo.ResultadoCalculo;
import com.sofka.cotizador.domain.port.CatalogoTarifasRepository;
import com.sofka.cotizador.domain.port.CotizacionRepository;
import com.sofka.cotizador.domain.service.CalculoPrimaService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EjecutarCalculoUseCase {

    private final CotizacionRepository cotizacionRepository;
    private final CatalogoTarifasRepository catalogoTarifasRepository;
    private final CalculoPrimaService calculoPrimaService;

    public EjecutarCalculoUseCase(CotizacionRepository cotizacionRepository,
                                   CatalogoTarifasRepository catalogoTarifasRepository,
                                   CalculoPrimaService calculoPrimaService) {
        this.cotizacionRepository = cotizacionRepository;
        this.catalogoTarifasRepository = catalogoTarifasRepository;
        this.calculoPrimaService = calculoPrimaService;
    }

    @Transactional
    public EjecutarCalculoResult ejecutar(EjecutarCalculoCommand command) {
        Cotizacion cotizacion = cotizacionRepository.findByNumeroFolio(command.numeroFolio())
                .orElseThrow(() -> new FolioNotFoundException(command.numeroFolio()));

        if (cotizacion.getVersion() != command.versionEsperada()) {
            throw new VersionConflictException(
                    command.numeroFolio(), cotizacion.getVersion(), command.versionEsperada());
        }

        OpcionesCobertura coberturas = cotizacion.getOpcionesCobertura() != null
                ? cotizacion.getOpcionesCobertura()
                : OpcionesCobertura.defaults();

        List<PrimaPorUbicacion> primasPorUbicacion = cotizacion.getUbicaciones().stream()
                .map(u -> calculoPrimaService.calcularUbicacion(u, coberturas, catalogoTarifasRepository))
                .toList();

        boolean hayCalculables = primasPorUbicacion.stream().anyMatch(PrimaPorUbicacion::calculada);
        if (!hayCalculables) {
            throw new SinUbicacionesCalculablesException(primasPorUbicacion);
        }

        BigDecimal primaNeta = primasPorUbicacion.stream()
                .filter(PrimaPorUbicacion::calculada)
                .map(PrimaPorUbicacion::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        ParametrosCalculo params = catalogoTarifasRepository.findParametrosCalculo();
        BigDecimal primaComercial = primaNeta.multiply(params.factorComercial())
                .setScale(2, RoundingMode.HALF_UP);

        ResultadoCalculo resultado = new ResultadoCalculo(
                primaNeta, primaComercial, params.factorComercial(),
                primasPorUbicacion, LocalDateTime.now());

        // actualizarResultadoCalculo already sets estado=CALCULADO and increments version by 1
        Cotizacion cotizacionCalculada = cotizacion.actualizarResultadoCalculo(resultado);
        Cotizacion guardada = cotizacionRepository.save(cotizacionCalculada);

        return new EjecutarCalculoResult(resultado, guardada);
    }
}
