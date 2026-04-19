package com.sofka.cotizador.infrastructure.persistence.catalogo;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cotizacion_parametros")
public class CotizacionParametrosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "factor_comercial")
    private BigDecimal factorComercial;

    @Column(name = "tasa_extension")
    private BigDecimal tasaExtension;

    @Column(name = "tasa_remocion")
    private BigDecimal tasaRemocion;

    @Column(name = "tasa_gastos_ext")
    private BigDecimal tasaGastosExt;

    @Column(name = "tasa_perdida_rentas")
    private BigDecimal tasaPerdidaRentas;

    @Column(name = "tasa_bi")
    private BigDecimal tasaBi;

    @Column(name = "tasa_dinero")
    private BigDecimal tasaDinero;

    @Column(name = "tasa_vidrios")
    private BigDecimal tasaVidrios;

    @Column(name = "tasa_anuncios")
    private BigDecimal tasaAnuncios;

    public Integer getId() { return id; }
    public BigDecimal getFactorComercial() { return factorComercial; }
    public BigDecimal getTasaExtension() { return tasaExtension; }
    public BigDecimal getTasaRemocion() { return tasaRemocion; }
    public BigDecimal getTasaGastosExt() { return tasaGastosExt; }
    public BigDecimal getTasaPerdidaRentas() { return tasaPerdidaRentas; }
    public BigDecimal getTasaBi() { return tasaBi; }
    public BigDecimal getTasaDinero() { return tasaDinero; }
    public BigDecimal getTasaVidrios() { return tasaVidrios; }
    public BigDecimal getTasaAnuncios() { return tasaAnuncios; }
}
