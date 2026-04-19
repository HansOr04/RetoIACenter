package com.sofka.cotizador.infrastructure.persistence.catalogo;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cotizacion_tarifas_cat_tev")
public class CotizacionTarifaCatTevEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "zona_tev")
    private String zonaTev;

    private BigDecimal tasa;

    public Integer getId() { return id; }
    public String getZonaTev() { return zonaTev; }
    public BigDecimal getTasa() { return tasa; }
}
