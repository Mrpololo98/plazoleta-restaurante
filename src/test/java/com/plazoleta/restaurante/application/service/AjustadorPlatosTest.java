package com.plazoleta.restaurante.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.domain.model.TipoDePlato;
import com.plazoleta.restaurante.domain.spi.RepositorioPlatosPuerto;
import com.plazoleta.restaurante.domain.spi.RepositorioRestaurantesPuerto;
import com.plazoleta.restaurante.domain.spi.UsuariosRemotosPuerto;

/**
 * Pruebas unitarias del caso de uso HU4 (corrección de precio y descripción).
 */
@ExtendWith(MockitoExtension.class)
class AjustadorPlatosTest {

    @Mock
    private RepositorioPlatosPuerto repositorioPlatos;

    @Mock
    private RepositorioRestaurantesPuerto repositorioRestaurantes;

    @Mock
    private UsuariosRemotosPuerto usuariosRemotos;

    private AjustadorPlatos ajustador;

    @BeforeEach
    void preparar() {
        VerificadorPropiedadRestaurante verificador =
                new VerificadorPropiedadRestaurante(repositorioRestaurantes, usuariosRemotos);
        ajustador = new AjustadorPlatos(repositorioPlatos, verificador);
    }

    private Plato platoGuardado() {
        return new Plato(5L, "Parrillada de la Casa", TipoDePlato.PRINCIPAL,
                "Descripción vieja", BigDecimal.valueOf(39500),
                "https://img.demo-plazoleta.co/parrillada.png", 1L, true);
    }

    private Restaurante restauranteDe(Long idPropietario) {
        return new Restaurante(1L, "La Brasa Dorada", "900112233",
                "Avenida Siempre Viva 742", "+573002221001",
                "https://img.demo-plazoleta.co/logo.png", idPropietario);
    }

    @Test
    void debeCorregirSoloElPrecioYLaDescripcion() {
        when(repositorioPlatos.obtenerPorId(5L)).thenReturn(Optional.of(platoGuardado()));
        when(repositorioRestaurantes.obtenerPorId(1L))
                .thenReturn(Optional.of(restauranteDe(2L)));
        when(repositorioPlatos.guardar(any(Plato.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Plato ajustado = ajustador.ejecutar(5L, BigDecimal.valueOf(41000),
                "Parrillada mixta renovada", 2L);

        assertEquals(0, BigDecimal.valueOf(41000).compareTo(ajustado.precio()));
        assertEquals("Parrillada mixta renovada", ajustado.descripcion());
        // HU4: la categoría, la imagen y la disponibilidad quedan intactas
        assertEquals(TipoDePlato.PRINCIPAL, ajustado.categoria());
        assertThat(ajustado.urlImagen()).contains("parrillada");
        assertEquals(true, ajustado.activo());
        verify(usuariosRemotos).confirmarPropietario(2L);
    }

    @Test
    void debeFallarCuandoElPlatoNoExiste() {
        when(repositorioPlatos.obtenerPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ajustador.ejecutar(404L, BigDecimal.TEN, "Nueva", 2L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("404");
        verifyNoInteractions(repositorioRestaurantes, usuariosRemotos);
    }

    @Test
    void debeFallarCuandoElPlatoEsDeUnRestauranteAjeno() {
        when(repositorioPlatos.obtenerPorId(5L)).thenReturn(Optional.of(platoGuardado()));
        when(repositorioRestaurantes.obtenerPorId(1L))
                .thenReturn(Optional.of(restauranteDe(3L)));

        assertThatThrownBy(() -> ajustador.ejecutar(5L, BigDecimal.TEN, "Nueva", 2L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no pertenece");
        verifyNoInteractions(usuariosRemotos);
    }

    @Test
    void debeRechazarElPrecioNegativoAntesDeTocarElRepositorio() {
        assertThatThrownBy(() -> ajustador.ejecutar(5L,
                BigDecimal.valueOf(-1), "Nueva", 2L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("número entero");
        verifyNoInteractions(repositorioPlatos, repositorioRestaurantes, usuariosRemotos);
    }
}
