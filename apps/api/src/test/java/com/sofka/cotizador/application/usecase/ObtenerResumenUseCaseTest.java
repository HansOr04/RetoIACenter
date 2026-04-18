package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.EstadoCotizacion;
import com.sofka.cotizador.domain.model.Folio;
import com.sofka.cotizador.domain.model.ubicacion.AlertaBloqueante;
import com.sofka.cotizador.domain.model.ubicacion.CodigoAlertaBloqueante;
import com.sofka.cotizador.domain.model.ubicacion.EstadoValidacionUbicacion;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;
import com.sofka.cotizador.domain.port.CotizacionRepository;
import com.sofka.cotizador.domain.port.FolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerResumenUseCaseTest {

    @Mock
    private FolioRepository folioRepository;

    @Mock
    private CotizacionRepository cotizacionRepository;

    private ObtenerResumenUbicacionesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ObtenerResumenUbicacionesUseCase(folioRepository, cotizacionRepository);
    }

    @Test
    void debeCalcularResumenConMixDeUbicaciones() {
        String numeroFolio = "F2026-0001";
        when(folioRepository.findByNumeroFolio(numeroFolio)).thenReturn(Optional.of(folioBasico(numeroFolio)));

        Ubicacion valida = Ubicacion.builder()
                .indice(0)
                .nombreUbicacion("Bodega A")
                .estadoValidacion(EstadoValidacionUbicacion.VALIDO)
                .alertasBloqueantes(Collections.emptyList())
                .build();

        AlertaBloqueante alerta = new AlertaBloqueante(
                CodigoAlertaBloqueante.CODIGO_POSTAL_INVALIDO.name(),
                "CP inválido",
                "codigoPostal"
        );
        Ubicacion incompleta = Ubicacion.builder()
                .indice(1)
                .nombreUbicacion("Bodega B")
                .estadoValidacion(EstadoValidacionUbicacion.INCOMPLETO)
                .alertasBloqueantes(List.of(alerta))
                .build();

        Cotizacion cotizacion = new Cotizacion(numeroFolio, List.of(valida, incompleta), 2, LocalDateTime.now());
        when(cotizacionRepository.findByNumeroFolio(numeroFolio)).thenReturn(Optional.of(cotizacion));

        ObtenerResumenUbicacionesUseCase.ResumenUbicaciones resumen = useCase.ejecutar(numeroFolio);

        assertThat(resumen.total()).isEqualTo(2);
        assertThat(resumen.completas()).isEqualTo(1);
        assertThat(resumen.incompletas()).isEqualTo(1);
        assertThat(resumen.calculables()).isEqualTo(1);
        assertThat(resumen.indicesIncompletos()).containsExactly(1);
        assertThat(resumen.detalleIncompletas()).hasSize(1);
        assertThat(resumen.detalleIncompletas().get(0).indice()).isEqualTo(1);
        assertThat(resumen.detalleIncompletas().get(0).alertas()).hasSize(1);
    }

    @Test
    void debeRetornarResumenVacioCuandoNoCotizacion() {
        String numeroFolio = "F2026-0002";
        when(folioRepository.findByNumeroFolio(numeroFolio)).thenReturn(Optional.of(folioBasico(numeroFolio)));
        when(cotizacionRepository.findByNumeroFolio(numeroFolio)).thenReturn(Optional.empty());

        ObtenerResumenUbicacionesUseCase.ResumenUbicaciones resumen = useCase.ejecutar(numeroFolio);

        assertThat(resumen.total()).isEqualTo(0);
        assertThat(resumen.completas()).isEqualTo(0);
        assertThat(resumen.incompletas()).isEqualTo(0);
        assertThat(resumen.calculables()).isEqualTo(0);
        assertThat(resumen.indicesIncompletos()).isEmpty();
        assertThat(resumen.detalleIncompletas()).isEmpty();
    }

    private Folio folioBasico(String numeroFolio) {
        return Folio.builder()
                .id(UUID.randomUUID().toString())
                .numeroFolio(numeroFolio)
                .estado(EstadoCotizacion.BORRADOR)
                .version(1)
                .fechaCreacion(LocalDateTime.now())
                .fechaUltimaActualizacion(LocalDateTime.now())
                .build();
    }
}
