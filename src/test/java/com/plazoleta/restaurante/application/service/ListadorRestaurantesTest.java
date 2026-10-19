package com.plazoleta.restaurante.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.model.HojaRestaurantes;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.domain.spi.RepositorioRestaurantesPuerto;

/**
 * Pruebas unitarias del caso de uso HU9 (listado de restaurantes).
 */
@ExtendWith(MockitoExtension.class)
class ListadorRestaurantesTest {

    @Mock
    private RepositorioRestaurantesPuerto repositorioRestaurantes;

    private ListadorRestaurantes listador;

    @BeforeEach
    void preparar() {
        listador = new ListadorRestaurantes(repositorioRestaurantes);
    }

    @Test
    void debeEntregarLaHojaAlfabeticaQueProduceElRepositorio() {
        HojaRestaurantes hoja = new HojaRestaurantes(List.of(
                new Restaurante(1L, "La Brasa Dorada", "900112233", "Avenida 742",
                        "+573002221001", "https://img.demo-plazoleta.co/a.png", 2L),
                new Restaurante(2L, "Wok Express", "900445566", "Carrera 45",
                        "+573002221002", "https://img.demo-plazoleta.co/b.png", 3L)),
                0, 2, 2L);
        when(repositorioRestaurantes.listarAlfabeticamente(0, 2)).thenReturn(hoja);

        HojaRestaurantes resultado = listador.ejecutar(0, 2);

        assertSame(hoja, resultado);
        assertEquals(1, resultado.totalHojas());
    }

    @Test
    void debeRechazarNumerosDePaginaNegativos() {
        assertThatThrownBy(() -> listador.ejecutar(-1, 10))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("negativo");
        verifyNoInteractions(repositorioRestaurantes);
    }

    @Test
    void debeRechazarCantidadesMenoresQueUno() {
        assertThatThrownBy(() -> listador.ejecutar(0, 0))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("cantidad");
        verifyNoInteractions(repositorioRestaurantes);
    }
}
