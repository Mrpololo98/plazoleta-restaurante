package com.plazoleta.restaurante.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.domain.model.SolicitudCuentaEmpleado;
import com.plazoleta.restaurante.domain.spi.RepositorioEmpleadosPuerto;
import com.plazoleta.restaurante.domain.spi.RepositorioRestaurantesPuerto;
import com.plazoleta.restaurante.domain.spi.UsuariosRemotosPuerto;

/**
 * Pruebas unitarias del caso de uso HU6 (contratación de empleados).
 */
@ExtendWith(MockitoExtension.class)
class VinculadorEmpleadosTest {

    @Mock
    private RepositorioRestaurantesPuerto repositorioRestaurantes;

    @Mock
    private UsuariosRemotosPuerto usuariosRemotos;

    @Mock
    private RepositorioEmpleadosPuerto repositorioEmpleados;

    private VinculadorEmpleados vinculador;

    @BeforeEach
    void preparar() {
        VerificadorPropiedadRestaurante verificador =
                new VerificadorPropiedadRestaurante(repositorioRestaurantes, usuariosRemotos);
        vinculador = new VinculadorEmpleados(verificador, usuariosRemotos, repositorioEmpleados);
    }

    private SolicitudCuentaEmpleado solicitudValida() {
        return new SolicitudCuentaEmpleado("Tomás", "Quiñones", "70225566",
                "+573005698325", "tomas.quinones@demo-plazoleta.co", "Empleado2026*");
    }

    private Restaurante restauranteDe(Long idPropietario) {
        return new Restaurante(1L, "La Brasa Dorada", "900112233",
                "Avenida Siempre Viva 742", "+573002221001",
                "https://img.demo-plazoleta.co/logo.png", idPropietario);
    }

    @Test
    void debeContratarGuardandoElVinculoConLaCuentaRemota() {
        when(repositorioRestaurantes.obtenerPorId(1L))
                .thenReturn(Optional.of(restauranteDe(2L)));
        when(usuariosRemotos.registrarCuentaEmpleado(any(SolicitudCuentaEmpleado.class)))
                .thenReturn(21L);
        when(repositorioEmpleados.guardar(any(Empleado.class))).thenAnswer(invocacion -> {
            Empleado entrante = invocacion.getArgument(0);
            return new Empleado(4L, entrante.idUsuario(), entrante.idRestaurante());
        });

        Empleado vinculo = vinculador.ejecutar(solicitudValida(), 1L, 2L);

        assertEquals(21L, vinculo.idUsuario());
        assertEquals(1L, vinculo.idRestaurante());
        verify(usuariosRemotos).confirmarPropietario(2L);
    }

    @Test
    void debeFallarCuandoElRestauranteNoExiste() {
        when(repositorioRestaurantes.obtenerPorId(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vinculador.ejecutar(solicitudValida(), 9L, 2L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("9");
        verify(usuariosRemotos, never()).registrarCuentaEmpleado(any());
        verifyNoInteractions(repositorioEmpleados);
    }

    @Test
    void debeFallarCuandoElRestauranteEsDeOtroPropietario() {
        when(repositorioRestaurantes.obtenerPorId(1L))
                .thenReturn(Optional.of(restauranteDe(3L)));

        assertThatThrownBy(() -> vinculador.ejecutar(solicitudValida(), 1L, 2L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no pertenece");
        verify(usuariosRemotos, never()).registrarCuentaEmpleado(any());
        verifyNoInteractions(repositorioEmpleados);
    }

    @Test
    void debeRechazarLaSolicitudSinCorreo() {
        SolicitudCuentaEmpleado sinCorreo = new SolicitudCuentaEmpleado(
                "Tomás", "Quiñones", "70225566", "+573005698325", "  ", "Empleado2026*");

        assertThatThrownBy(() -> vinculador.ejecutar(sinCorreo, 1L, 2L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("correo");
        verifyNoInteractions(repositorioRestaurantes, usuariosRemotos, repositorioEmpleados);
    }
}
