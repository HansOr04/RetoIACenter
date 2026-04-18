package com.sofka.cotizador.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// HU-001 — entidad JPA; status como String para evitar problemas con enums de PostgreSQL
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
}
