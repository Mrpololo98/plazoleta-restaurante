package com.plazoleta.restaurante.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.restaurante.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.model.TipoDePlato;
import com.plazoleta.restaurante.domain.spi.RepositorioPlatosPuerto;

/**
 * Pruebas del caso de uso de integración ConsultarPlato, con el que el
 * microservicio de pedidos valida los platos de una orden (HU11).
 */
@ExtendWith(MockitoExtension.class)
class ConsultorPlatosTest {

    @Mock
    private RepositorioPlatosPuerto repositorioPlatos;

    private ConsultorPlatos consultor;

    @BeforeEach
    void preparar() {
        consultor = new ConsultorPlatos(repositorioPlatos);
    }

    @Test
    void debeDevolverLaFichaDelPlatoPedido() {
        Plato parrillada = new Plato(1L, "Parrillada de la Casa", TipoDePlato.PRINCIPAL,
                "Parrillada mixta", BigDecimal.valueOf(39500),
                "https://img.demo-plazoleta.co/parrillada.png", 1L, true);
        when(repositorioPlatos.obtenerPorId(1L)).thenReturn(Optional.of(parrillada));

        Plato resultado = consultor.ejecutar(1L);

        assertEquals("Parrillada de la Casa", resultado.nombre());
        assertEquals(1L, resultado.idRestaurante());
    }

    @Test
    void debeRechazarPlatosInexistentes() {
        when(repositorioPlatos.obtenerPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultor.ejecutar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("plato");
    }
}
