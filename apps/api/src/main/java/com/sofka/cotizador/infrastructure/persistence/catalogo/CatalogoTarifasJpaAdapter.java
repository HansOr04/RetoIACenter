package com.sofka.cotizador.infrastructure.persistence.catalogo;

import com.sofka.cotizador.domain.model.calculo.ParametrosCalculo;
import com.sofka.cotizador.domain.port.CatalogoTarifasRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class CatalogoTarifasJpaAdapter implements CatalogoTarifasRepository {

    private final CotizacionParametrosJpaRepository parametrosRepo;
    private final CotizacionTarifaIncendioJpaRepository tarifaIncendioRepo;
    private final CotizacionTarifaCatTevJpaRepository catTevRepo;
    private final CotizacionTarifaCatFhmJpaRepository catFhmRepo;
    private final CotizacionFactorEquipoJpaRepository equipoRepo;

    public CatalogoTarifasJpaAdapter(
            CotizacionParametrosJpaRepository parametrosRepo,
            CotizacionTarifaIncendioJpaRepository tarifaIncendioRepo,
            CotizacionTarifaCatTevJpaRepository catTevRepo,
            CotizacionTarifaCatFhmJpaRepository catFhmRepo,
            CotizacionFactorEquipoJpaRepository equipoRepo) {
        this.parametrosRepo = parametrosRepo;
        this.tarifaIncendioRepo = tarifaIncendioRepo;
        this.catTevRepo = catTevRepo;
        this.catFhmRepo = catFhmRepo;
        this.equipoRepo = equipoRepo;
    }

    @Override
    public Optional<TarifaIncendio> findTarifaIncendio(String claveIncendio, String tipoConstructivo) {
        return tarifaIncendioRepo
                .findByClaveIncendioAndTipoConstructivo(claveIncendio, tipoConstructivo)
                .map(e -> new TarifaIncendio(e.getTasaEdificios(), e.getTasaContenidos()));
    }

    @Override
    public Optional<BigDecimal> findTasaCatTev(String zonaTev) {
        return catTevRepo.findByZonaTev(zonaTev)
                .map(CotizacionTarifaCatTevEntity::getTasa);
    }

    @Override
    public Optional<BigDecimal> findTasaCatFhm(String zonaFhm) {
        return catFhmRepo.findByZonaFhm(zonaFhm)
                .map(CotizacionTarifaCatFhmEntity::getTasa);
    }

    @Override
    public Optional<BigDecimal> findFactorEquipoElectronico(String clase, int nivel) {
        return equipoRepo.findByClaseAndNivel(clase, nivel)
                .map(CotizacionFactorEquipoEntity::getFactor);
    }

    @Override
    public ParametrosCalculo findParametrosCalculo() {
        CotizacionParametrosEntity e = parametrosRepo.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay parámetros de cálculo configurados"));
        return new ParametrosCalculo(
                e.getFactorComercial(),
                e.getTasaExtension(),
                e.getTasaRemocion(),
                e.getTasaGastosExt(),
                e.getTasaPerdidaRentas(),
                e.getTasaBi(),
                e.getTasaDinero(),
                e.getTasaVidrios(),
                e.getTasaAnuncios()
        );
    }
}
