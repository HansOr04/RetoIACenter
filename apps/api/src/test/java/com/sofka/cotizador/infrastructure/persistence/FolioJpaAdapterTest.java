package com.sofka.cotizador.infrastructure.persistence;

import com.sofka.cotizador.domain.model.DatosGenerales;
import com.sofka.cotizador.domain.model.EstadoCotizacion;
import com.sofka.cotizador.domain.model.Folio;
import com.sofka.cotizador.domain.model.LayoutUbicaciones;
import com.sofka.cotizador.domain.model.SeccionesAplican;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FolioJpaAdapterTest {

    @Mock
    private FolioJpaRepository jpaRepository;

    private FolioJpaAdapter adapter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        adapter = new FolioJpaAdapter(jpaRepository);
    }

    @Test
    void testSave() {
        Folio folio = Folio.builder()
                .id(UUID.randomUUID().toString())
                .numeroFolio("F-100")
                .idempotencyKey("key-1")
                .estado(EstadoCotizacion.BORRADOR)
                .tipoNegocio("Nuevo")
                .codigoAgente("A-1")
                .version(1)
                .fechaCreacion(LocalDateTime.now())
                .fechaUltimaActualizacion(LocalDateTime.now())
                .datosGenerales(new DatosGenerales("Cliente", "rfc", "email@test.com", "555-1234", "Casa", "Habitacional", 2000, 2, "Desc"))
                .layoutUbicaciones(new LayoutUbicaciones(2, new SeccionesAplican(true, true, true, true)))
                .build();

        FolioJpaEntity entity = FolioJpaEntity.builder()
                .id(UUID.fromString(folio.getId()))
                .numeroFolio(folio.getNumeroFolio())
                .idempotencyKey(folio.getIdempotencyKey())
                .status(folio.getEstado().name())
                .tipoNegocio(folio.getTipoNegocio())
                .agentId(folio.getCodigoAgente())
                .version(folio.getVersion())
                .createdAt(folio.getFechaCreacion())
                .updatedAt(folio.getFechaUltimaActualizacion())
                .datosGenerales(folio.getDatosGenerales())
                .layoutUbicaciones(folio.getLayoutUbicaciones())
                .build();

        when(jpaRepository.save(any(FolioJpaEntity.class))).thenReturn(entity);

        Folio saved = adapter.save(folio);

        assertNotNull(saved);
        assertEquals("F-100", saved.getNumeroFolio());
        assertEquals("key-1", saved.getIdempotencyKey());
        
        verify(jpaRepository).save(any(FolioJpaEntity.class));
    }

    @Test
    void testFindByIdempotencyKey() {
        FolioJpaEntity entity = FolioJpaEntity.builder()
                .id(UUID.randomUUID())
                .numeroFolio("F-200")
                .status("BORRADOR")
                .build();

        when(jpaRepository.findByIdempotencyKey("key-2")).thenReturn(Optional.of(entity));

        Optional<Folio> found = adapter.findByIdempotencyKey("key-2");

        assertTrue(found.isPresent());
        assertEquals("F-200", found.get().getNumeroFolio());
    }

    @Test
    void testFindByNumeroFolio() {
        FolioJpaEntity entity = FolioJpaEntity.builder()
                .id(UUID.randomUUID())
                .numeroFolio("F-300")
                .status("BORRADOR")
                .build();

        when(jpaRepository.findByNumeroFolio("F-300")).thenReturn(Optional.of(entity));

        Optional<Folio> found = adapter.findByNumeroFolio("F-300");

        assertTrue(found.isPresent());
        assertEquals("F-300", found.get().getNumeroFolio());
    }
}
