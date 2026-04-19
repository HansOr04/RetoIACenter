package com.sofka.cotizador.infrastructure.persistence.catalogo;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cotizacion_tarifas_cat_fhm")
public class CotizacionTarifaCatFhmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "zona_fhm")
    private String zonaFhm;

    private BigDecimal tasa;

    public Integer getId() { return id; }
    public String getZonaFhm() { return zonaFhm; }
    public BigDecimal getTasa() { return tasa; }
}
