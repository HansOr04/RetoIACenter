package com.sofka.cotizador.infrastructure.persistence.catalogo;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cotizacion_tarifas_incendio")
public class CotizacionTarifaIncendioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "clave_incendio")
    private String claveIncendio;

    @Column(name = "tipo_constructivo")
    private String tipoConstructivo;

    @Column(name = "tasa_edificios")
    private BigDecimal tasaEdificios;

    @Column(name = "tasa_contenidos")
    private BigDecimal tasaContenidos;

    public Integer getId() { return id; }
    public String getClaveIncendio() { return claveIncendio; }
    public String getTipoConstructivo() { return tipoConstructivo; }
    public BigDecimal getTasaEdificios() { return tasaEdificios; }
    public BigDecimal getTasaContenidos() { return tasaContenidos; }
}
