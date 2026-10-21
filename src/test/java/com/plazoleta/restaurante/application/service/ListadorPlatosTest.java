package com.plazoleta.restaurante.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.restaurante.domain.model.HojaPlatos;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.domain.model.TipoDePlato;
import com.plazoleta.restaurante.domain.spi.RepositorioPlatosPuerto;
import com.plazoleta.restaurante.domain.spi.RepositorioRestaurantesPuerto;

/**
 * Pruebas unitarias del caso de uso HU10 (menú de un restaurante).
 */
@ExtendWith(MockitoExtension.class)
class ListadorPlatosTest {

    private static final Restaurante BRASA_DORADA = new Restaurante(
            1L, "La Brasa Dorada", "900112233", "Avenida 742",
            "+573002221001", "https://img.demo-plazoleta.co/a.png", 2L);

    @Mock
    private RepositorioPlatosPuerto repositorioPlatos;

    @Mock
    private RepositorioRestaurantesPuerto repositorioRestaurantes;

    private ListadorPlatos listador;

    @BeforeEach
    void preparar() {
        listador = new ListadorPlatos(repositorioPlatos, repositorioRestaurantes);
    }

    @Test
    void debeEntregarLaCartaQueProduceElRepositorio() {
        HojaPlatos hoja = new HojaPlatos(List.of(
                plato(1L, "Arepa Rellena", TipoDePlato.ENTRADA),
                plato(2L, "Parrillada de la Casa", TipoDePlato.PRINCIPAL)),
                0, 2, 2L);
        when(repositorioRestaurantes.obtenerPorId(1L)).thenReturn(Optional.of(BRASA_DORADA));
        when(repositorioPlatos.listarCarta(1L, null, 0, 2)).thenReturn(hoja);

        HojaPlatos resultado = listador.ejecutar(1L, null, 0, 2);

        assertSame(hoja, resultado);
        assertEquals(1, resultado.totalHojas());
    }

    @Test
    void debePasarElFiltroDeCategoriaTalCualAlRepositorio() {
        HojaPlatos hoja = new HojaPlatos(List.of(plato(2L, "Parrillada de la Casa", TipoDePlato.PRINCIPAL)),
                0, 10, 1L);
        when(repositorioRestaurantes.obtenerPorId(1L)).thenReturn(Optional.of(BRASA_DORADA));
        when(repositorioPlatos.listarCarta(1L, TipoDePlato.PRINCIPAL, 0, 10)).thenReturn(hoja);

        HojaPlatos resultado = listador.ejecutar(1L, TipoDePlato.PRINCIPAL, 0, 10);

        assertSame(hoja, resultado);
        assertEquals(TipoDePlato.PRINCIPAL, resultado.contenido().get(0).categoria());
    }

    @Test
    void debeRechazarRestaurantesInexistentes() {
        when(repositorioRestaurantes.obtenerPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listador.ejecutar(99L, null, 0, 10))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("restaurante");
    }

    @Test
    void debeRechazarPaginacionesInvalidas() {
        assertThatThrownBy(() -> listador.ejecutar(1L, null, -1, 10))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("negativo");

        assertThatThrownBy(() -> listador.ejecutar(1L, null, 0, 0))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("cantidad");

        verifyNoInteractions(repositorioPlatos);
    }

    private Plato plato(Long id, String nombre, TipoDePlato categoria) {
        return new Plato(id, nombre, categoria, "Descripcion de " + nombre,
                BigDecimal.valueOf(15000), "https://img.demo-plazoleta.co/" + id + ".png",
                1L, true);
    }
}
