package com.sofka.cotizador.infrastructure.persistence;

import com.sofka.cotizador.domain.model.EstadoCotizacion;
import com.sofka.cotizador.domain.model.Folio;
import com.sofka.cotizador.domain.port.FolioRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FolioJpaAdapter implements FolioRepository {

    private final FolioJpaRepository jpaRepository;

    public FolioJpaAdapter(FolioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Folio save(Folio folio) {
        FolioJpaEntity entity = toEntity(folio);
        FolioJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Folio> findByIdempotencyKey(String idempotencyKey) {
        return jpaRepository.findByIdempotencyKey(idempotencyKey).map(this::toDomain);
    }

    @Override
    public Optional<Folio> findByNumeroFolio(String numeroFolio) {
        return jpaRepository.findByNumeroFolio(numeroFolio).map(this::toDomain);
    }

    private FolioJpaEntity toEntity(Folio folio) {
        return FolioJpaEntity.builder()
                .id(folio.getId())
                .numeroFolio(folio.getNumeroFolio())
                .idempotencyKey(folio.getIdempotencyKey())
                .status(folio.getEstado().name())
                .tipoNegocio(folio.getTipoNegocio())
                .agentId(folio.getCodigoAgente())
                .version(folio.getVersion())
                .createdAt(folio.getFechaCreacion())
                .updatedAt(folio.getFechaUltimaActualizacion())
                .datosGenerales(folio.getDatosGenerales())
                .build();
    }

    private Folio toDomain(FolioJpaEntity entity) {
        return Folio.builder()
                .id(entity.getId())
                .numeroFolio(entity.getNumeroFolio())
                .idempotencyKey(entity.getIdempotencyKey())
                .estado(EstadoCotizacion.valueOf(entity.getStatus()))
                .tipoNegocio(entity.getTipoNegocio())
                .codigoAgente(entity.getAgentId())
                .version(entity.getVersion())
                .fechaCreacion(entity.getCreatedAt())
                .fechaUltimaActualizacion(entity.getUpdatedAt())
                .datosGenerales(entity.getDatosGenerales())
                .build();
    }
}
