package com.sofka.cotizador.domain.service;

import com.sofka.cotizador.domain.model.OpcionesCobertura;
import com.sofka.cotizador.domain.model.calculo.DesgloseComponentes;
import com.sofka.cotizador.domain.model.calculo.ParametrosCalculo;
import com.sofka.cotizador.domain.model.calculo.PrimaPorUbicacion;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;
import com.sofka.cotizador.domain.port.CatalogoTarifasRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

@Service
public class CalculoPrimaService {

    private static final BigDecimal VALOR_ASEGURADO_DEFAULT = new BigDecimal("1000000.00");
    private static final BigDecimal ROBO_FACTOR = new BigDecimal("0.15");

    public PrimaPorUbicacion calcularUbicacion(
            Ubicacion ubicacion,
            OpcionesCobertura coberturas,
            CatalogoTarifasRepository catalogo) {

        if (!ubicacion.esCalculable()) {
            return PrimaPorUbicacion.incalculable(ubicacion.getIndice(), ubicacion.getAlertasBloqueantes());
        }

        ParametrosCalculo params = catalogo.findParametrosCalculo();
        BigDecimal valorAsegurado = VALOR_ASEGURADO_DEFAULT;

        BigDecimal incEdif  = calcularIncendioEdificios(ubicacion, coberturas, catalogo, valorAsegurado);
        BigDecimal incCont  = calcularIncendioContenidos(ubicacion, coberturas, catalogo, valorAsegurado);
        BigDecimal ext      = calcularExtension(incEdif, incCont, coberturas, params);
        BigDecimal catTev   = calcularCatTev(ubicacion, coberturas, catalogo, valorAsegurado);
        BigDecimal catFhm   = calcularCatFhm(ubicacion, coberturas, catalogo, valorAsegurado);
        BigDecimal remocion = calcularRemocion(incEdif, coberturas, params);
        BigDecimal gastos   = calcularGastosExt(incEdif, coberturas, params);
        BigDecimal rentas   = calcularPerdidaRentas(incEdif, coberturas, params);
        BigDecimal bi       = calcularBi(incEdif, coberturas, params);
        BigDecimal equipo   = calcularEquipoElectronico(ubicacion, coberturas, catalogo, valorAsegurado);
        BigDecimal robo     = calcularRobo(incCont, coberturas);
        BigDecimal dinero   = calcularDineroValores(incCont, coberturas, params);
        BigDecimal vidrios  = calcularVidrios(incEdif, coberturas, params);
        BigDecimal anuncios = calcularAnuncios(incEdif, coberturas, params);

        BigDecimal total = Stream.of(incEdif, incCont, ext, catTev, catFhm, remocion,
                        gastos, rentas, bi, equipo, robo, dinero, vidrios, anuncios)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        DesgloseComponentes desglose = new DesgloseComponentes(
                incEdif, incCont, ext, catTev, catFhm, remocion, gastos, rentas,
                bi, equipo, robo, dinero, vidrios, anuncios, total);

        return new PrimaPorUbicacion(ubicacion.getIndice(), true, total, desglose, List.of());
    }

    private BigDecimal calcularIncendioEdificios(Ubicacion u, OpcionesCobertura c,
                                                  CatalogoTarifasRepository cat, BigDecimal va) {
        if (!c.incendioEdificios()) return BigDecimal.ZERO;
        return cat.findTarifaIncendio(u.getGiro().claveIncendio(), u.getTipoConstructivo())
                .map(t -> va.multiply(t.tasaEdificios()).setScale(2, RoundingMode.HALF_UP))
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calcularIncendioContenidos(Ubicacion u, OpcionesCobertura c,
                                                   CatalogoTarifasRepository cat, BigDecimal va) {
        if (!c.incendioContenidos()) return BigDecimal.ZERO;
        return cat.findTarifaIncendio(u.getGiro().claveIncendio(), u.getTipoConstructivo())
                .map(t -> va.multiply(t.tasaContenidos()).setScale(2, RoundingMode.HALF_UP))
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calcularExtension(BigDecimal incEdif, BigDecimal incCont,
                                          OpcionesCobertura c, ParametrosCalculo p) {
        if (!c.extensionCobertura()) return BigDecimal.ZERO;
        return incEdif.add(incCont).multiply(p.tasaExtension()).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularCatTev(Ubicacion u, OpcionesCobertura c,
                                       CatalogoTarifasRepository cat, BigDecimal va) {
        if (!c.catTev() || u.getZonaCatastrofica() == null || u.getZonaCatastrofica().zonaTev() == null) {
            return BigDecimal.ZERO;
        }
        return cat.findTasaCatTev(u.getZonaCatastrofica().zonaTev())
                .map(t -> va.multiply(t).setScale(2, RoundingMode.HALF_UP))
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calcularCatFhm(Ubicacion u, OpcionesCobertura c,
                                       CatalogoTarifasRepository cat, BigDecimal va) {
        if (!c.catFhm() || u.getZonaCatastrofica() == null || u.getZonaCatastrofica().zonaFhm() == null) {
            return BigDecimal.ZERO;
        }
        return cat.findTasaCatFhm(u.getZonaCatastrofica().zonaFhm())
                .map(t -> va.multiply(t).setScale(2, RoundingMode.HALF_UP))
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calcularRemocion(BigDecimal incEdif, OpcionesCobertura c, ParametrosCalculo p) {
        if (!c.remocionEscombros()) return BigDecimal.ZERO;
        return incEdif.multiply(p.tasaRemocion()).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularGastosExt(BigDecimal incEdif, OpcionesCobertura c, ParametrosCalculo p) {
        if (!c.gastosExtraordinarios()) return BigDecimal.ZERO;
        return incEdif.multiply(p.tasaGastosExt()).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularPerdidaRentas(BigDecimal incEdif, OpcionesCobertura c, ParametrosCalculo p) {
        if (!c.perdidaRentas()) return BigDecimal.ZERO;
        return incEdif.multiply(p.tasaPerdidaRentas()).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularBi(BigDecimal incEdif, OpcionesCobertura c, ParametrosCalculo p) {
        if (!c.bi()) return BigDecimal.ZERO;
        return incEdif.multiply(p.tasaBi()).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularEquipoElectronico(Ubicacion u, OpcionesCobertura c,
                                                   CatalogoTarifasRepository cat, BigDecimal va) {
        if (!c.equipoElectronico()) return BigDecimal.ZERO;
        int nivel = u.getNivel() != null ? u.getNivel() : 1;
        return cat.findFactorEquipoElectronico("B", nivel)
                .map(f -> va.multiply(f).setScale(2, RoundingMode.HALF_UP))
                .orElseGet(() -> va.multiply(new BigDecimal("0.0045")).setScale(2, RoundingMode.HALF_UP));
    }

    private BigDecimal calcularRobo(BigDecimal incCont, OpcionesCobertura c) {
        if (!c.robo()) return BigDecimal.ZERO;
        return incCont.multiply(ROBO_FACTOR).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularDineroValores(BigDecimal incCont, OpcionesCobertura c, ParametrosCalculo p) {
        if (!c.dineroValores()) return BigDecimal.ZERO;
        return incCont.multiply(p.tasaDinero()).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularVidrios(BigDecimal incEdif, OpcionesCobertura c, ParametrosCalculo p) {
        if (!c.vidrios()) return BigDecimal.ZERO;
        return incEdif.multiply(p.tasaVidrios()).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularAnuncios(BigDecimal incEdif, OpcionesCobertura c, ParametrosCalculo p) {
        if (!c.anunciosLuminosos()) return BigDecimal.ZERO;
        return incEdif.multiply(p.tasaAnuncios()).setScale(2, RoundingMode.HALF_UP);
    }
}
