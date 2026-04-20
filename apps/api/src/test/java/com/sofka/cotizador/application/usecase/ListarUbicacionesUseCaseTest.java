package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;
import com.sofka.cotizador.domain.port.CotizacionRepository;
import com.sofka.cotizador.domain.port.FolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ListarUbicacionesUseCaseTest {

    @Mock
    private FolioRepository folioRepository;

    @Mock
    private CotizacionRepository cotizacionRepository;

    private ListarUbicacionesUseCase useCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        useCase = new ListarUbicacionesUseCase(folioRepository, cotizacionRepository);
    }

    @Test
    void happyPath_retornaListaUbicaciones_cuandoFolioExiste() {
        Cotizacion cotizacion = mock(Cotizacion.class);
        when(folioRepository.findByNumeroFolio("FOLIO-1")).thenReturn(Optional.of(mock(com.sofka.cotizador.domain.model.Folio.class)));
        when(cotizacionRepository.findByNumeroFolio("FOLIO-1")).thenReturn(Optional.of(cotizacion));
        when(cotizacion.getUbicaciones()).thenReturn(List.of(mock(Ubicacion.class), mock(Ubicacion.class)));

        var result = useCase.ejecutar("FOLIO-1");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(folioRepository).findByNumeroFolio("FOLIO-1");
        verify(cotizacionRepository).findByNumeroFolio("FOLIO-1");
    }

    @Test
    void retornaListaVacia_cuandoNoHayCotizacion() {
        when(folioRepository.findByNumeroFolio("FOLIO-2")).thenReturn(Optional.of(mock(com.sofka.cotizador.domain.model.Folio.class)));
        when(cotizacionRepository.findByNumeroFolio("FOLIO-2")).thenReturn(Optional.empty());

        var result = useCase.ejecutar("FOLIO-2");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void retornaListaVacia_cuandoNoHayUbicaciones() {
        Cotizacion cotizacion = mock(Cotizacion.class);
        when(folioRepository.findByNumeroFolio("FOLIO-3")).thenReturn(Optional.of(mock(com.sofka.cotizador.domain.model.Folio.class)));
        when(cotizacionRepository.findByNumeroFolio("FOLIO-3")).thenReturn(Optional.of(cotizacion));
        when(cotizacion.getUbicaciones()).thenReturn(List.of());

        var result = useCase.ejecutar("FOLIO-3");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void lanzaException_cuandoFolioNoExiste() {
        when(folioRepository.findByNumeroFolio("FOLIO-4")).thenReturn(Optional.empty());

        assertThrows(FolioNotFoundException.class, () -> useCase.ejecutar("FOLIO-4"));
    }
}
