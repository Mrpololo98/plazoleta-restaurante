package com.plazoleta.restaurante.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.restaurante.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.domain.spi.RepositorioRestaurantesPuerto;

/**
 * Pruebas del caso de uso de integración ConsultarRestaurante, con el que
 * el microservicio de pedidos valida al dueño en las métricas (HU18).
 */
@ExtendWith(MockitoExtension.class)
class ConsultorRestaurantesTest {

    @Mock
    private RepositorioRestaurantesPuerto repositorioRestaurantes;

    private ConsultorRestaurantes consultor;

    @BeforeEach
    void preparar() {
        consultor = new ConsultorRestaurantes(repositorioRestaurantes);
    }

    @Test
    void debeDevolverLaFichaDelRestaurante() {
        Restaurante brasaDorada = new Restaurante(1L, "La Brasa Dorada", "900112233",
                "Avenida 742", "+573002221001",
                "https://img.demo-plazoleta.co/brasa.png", 2L);
        when(repositorioRestaurantes.obtenerPorId(1L)).thenReturn(Optional.of(brasaDorada));

        Restaurante resultado = consultor.ejecutar(1L);

        assertEquals("La Brasa Dorada", resultado.nombre());
        assertEquals(2L, resultado.idPropietario());
    }

    @Test
    void debeRechazarRestaurantesInexistentes() {
        when(repositorioRestaurantes.obtenerPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultor.ejecutar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("restaurante");
    }
}
