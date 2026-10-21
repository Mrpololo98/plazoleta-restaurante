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
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.domain.spi.RepositorioEmpleadosPuerto;

/**
 * Pruebas del caso de uso de integración ConsultarEmpleado, con el que el
 * microservicio de pedidos filtra las órdenes del empleado (HU12).
 */
@ExtendWith(MockitoExtension.class)
class ConsultorEmpleadosTest {

    @Mock
    private RepositorioEmpleadosPuerto repositorioEmpleados;

    private ConsultorEmpleados consultor;

    @BeforeEach
    void preparar() {
        consultor = new ConsultorEmpleados(repositorioEmpleados);
    }

    @Test
    void debeDevolverElVinculoLaboralDeLaCuenta() {
        Empleado vinculo = new Empleado(1L, 5L, 1L);
        when(repositorioEmpleados.obtenerPorIdUsuario(5L)).thenReturn(Optional.of(vinculo));

        Empleado resultado = consultor.ejecutar(5L);

        assertEquals(1L, resultado.idRestaurante());
        assertEquals(5L, resultado.idUsuario());
    }

    @Test
    void debeRechazarCuentasSinVinculoLaboral() {
        when(repositorioEmpleados.obtenerPorIdUsuario(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultor.ejecutar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("empleado");
    }
}
