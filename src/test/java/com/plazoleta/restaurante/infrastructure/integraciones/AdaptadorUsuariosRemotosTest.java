package com.plazoleta.restaurante.infrastructure.integraciones;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.restaurante.domain.model.SolicitudCuentaEmpleado;
import com.plazoleta.restaurante.infrastructure.integraciones.remoto.CuentaUsuarioRemota;
import com.plazoleta.restaurante.infrastructure.integraciones.remoto.PeticionCuentaEmpleadoRemota;
import com.plazoleta.restaurante.infrastructure.integraciones.remoto.RespuestaCuentaRemota;

import feign.FeignException;
import feign.Request;

/**
 * Pruebas unitarias del adaptador hacia el microservicio de usuarios.
 */
@ExtendWith(MockitoExtension.class)
class AdaptadorUsuariosRemotosTest {

    @Mock
    private ClienteUsuariosApi clienteUsuarios;

    private AdaptadorUsuariosRemotos adaptador;

    @BeforeEach
    void preparar() {
        adaptador = new AdaptadorUsuariosRemotos(clienteUsuarios);
    }

    @Test
    void debeAceptarCuandoLaCuentaTieneRolPropietario() {
        when(clienteUsuarios.consultarCuenta(2L)).thenReturn(new CuentaUsuarioRemota(2L, "PROPIETARIO"));

        assertDoesNotThrow(() -> adaptador.confirmarPropietario(2L));
    }

    @Test
    void debeReportarCuentaInexistenteComoRecursoAusente() {
        when(clienteUsuarios.consultarCuenta(99L)).thenThrow(falloNotFound(99L));

        assertThatThrownBy(() -> adaptador.confirmarPropietario(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void debeRechazarCuentasConOtroRol() {
        when(clienteUsuarios.consultarCuenta(4L)).thenReturn(new CuentaUsuarioRemota(4L, "CLIENTE"));

        assertThatThrownBy(() -> adaptador.confirmarPropietario(4L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("rol de propietario");
    }

    @Test
    void debeDevolverElIdentificadorDeLaCuentaCreada() {
        when(clienteUsuarios.crearCuentaEmpleado(any(PeticionCuentaEmpleadoRemota.class)))
                .thenReturn(new RespuestaCuentaRemota(21L));
        SolicitudCuentaEmpleado solicitud = new SolicitudCuentaEmpleado("Tomás", "Quiñones",
                "70225566", "+573005698325", "tomas.quinones@demo-plazoleta.co", "Empleado2026*");

        Long idCuenta = adaptador.registrarCuentaEmpleado(solicitud);

        assertEquals(21L, idCuenta);
        verify(clienteUsuarios).crearCuentaEmpleado(argThat(peticion ->
                "tomas.quinones@demo-plazoleta.co".equals(peticion.correo())
                        && "Tomás".equals(peticion.nombre())));
    }

    @Test
    void debeConvertirElRechazoRemotoEnReglaDeNegocio() {
        when(clienteUsuarios.crearCuentaEmpleado(any(PeticionCuentaEmpleadoRemota.class)))
                .thenThrow(new FeignException.BadRequest("rechazado", peticionPost(),
                        "{\"mensaje\":\"Correo ya registrado\"}".getBytes(StandardCharsets.UTF_8),
                        Collections.emptyMap()));
        SolicitudCuentaEmpleado solicitud = new SolicitudCuentaEmpleado("Tomás", "Quiñones",
                "70225566", "+573005698325", "repetido@demo-plazoleta.co", "Empleado2026*");

        assertThatThrownBy(() -> adaptador.registrarCuentaEmpleado(solicitud))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("rechazado por el servicio de cuentas")
                .hasMessageContaining("Correo ya registrado");
    }

    private FeignException.NotFound falloNotFound(Long id) {
        return new FeignException.NotFound("ausente", peticionGet(id),
                null, Collections.emptyMap());
    }

    private Request peticionGet(Long id) {
        return Request.create(Request.HttpMethod.GET, "/usuarios/" + id,
                Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
    }

    private Request peticionPost() {
        return Request.create(Request.HttpMethod.POST, "/empleados",
                Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
    }
}
