package com.sofka.cotizador.domain.port;

import com.sofka.cotizador.domain.model.calculo.ParametrosCalculo;

import java.math.BigDecimal;
import java.util.Optional;

public interface CatalogoTarifasRepository {

    Optional<TarifaIncendio> findTarifaIncendio(String claveIncendio, String tipoConstructivo);

    Optional<BigDecimal> findTasaCatTev(String zonaTev);

    Optional<BigDecimal> findTasaCatFhm(String zonaFhm);

    Optional<BigDecimal> findFactorEquipoElectronico(String clase, int nivel);

    ParametrosCalculo findParametrosCalculo();

    record TarifaIncendio(BigDecimal tasaEdificios, BigDecimal tasaContenidos) {}
}
