package com.sofka.cotizador.infrastructure.persistence;

import com.sofka.cotizador.domain.model.DatosGenerales;
import com.sofka.cotizador.domain.model.LayoutUbicaciones;
import com.sofka.cotizador.infrastructure.persistence.converter.DatosGeneralesConverter;
import com.sofka.cotizador.infrastructure.persistence.converter.LayoutUbicacionesConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "folios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolioJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "numero_folio", unique = true, nullable = false, length = 20)
    private String numeroFolio;

    @Column(name = "idempotency_key", unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "tipo_negocio", length = 50)
    private String tipoNegocio;

    @Column(name = "agent_id", length = 50)
    private String agentId;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Convert(converter = DatosGeneralesConverter.class)
    @Column(name = "datos_generales", columnDefinition = "jsonb")
    private DatosGenerales datosGenerales;

    @Convert(converter = LayoutUbicacionesConverter.class)
    @Column(name = "layout_ubicaciones", columnDefinition = "jsonb")
    private LayoutUbicaciones layoutUbicaciones;
}
