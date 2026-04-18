package com.sofka.cotizador.infrastructure.persistence;

import com.sofka.cotizador.infrastructure.persistence.converter.CotizacionDatosConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cotizaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CotizacionJpaEntity {

    @Id
    @Column(name = "numero_folio", nullable = false, length = 50)
    private String numeroFolio;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "fecha_ultima_actualizacion", nullable = false)
    private LocalDateTime fechaUltimaActualizacion;

    @Column(name = "prima_neta", precision = 15, scale = 2)
    private BigDecimal primaNeta;

    @Column(name = "prima_comercial", precision = 15, scale = 2)
    private BigDecimal primaComercial;

    @Convert(converter = CotizacionDatosConverter.class)
    @Column(name = "datos", columnDefinition = "jsonb", nullable = false)
    private DatosCotizacion datos;
}
