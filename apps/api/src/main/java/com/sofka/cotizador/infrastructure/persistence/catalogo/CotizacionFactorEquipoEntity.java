package com.sofka.cotizador.infrastructure.persistence.catalogo;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cotizacion_factores_equipo")
public class CotizacionFactorEquipoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String clase;

    private Integer nivel;

    private BigDecimal factor;

    public Integer getId() { return id; }
    public String getClase() { return clase; }
    public Integer getNivel() { return nivel; }
    public BigDecimal getFactor() { return factor; }
}
