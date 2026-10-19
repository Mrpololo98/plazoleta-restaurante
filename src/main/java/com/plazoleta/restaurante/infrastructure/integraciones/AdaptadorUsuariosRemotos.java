package com.plazoleta.restaurante.infrastructure.integraciones;

import org.springframework.stereotype.Component;

import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.restaurante.domain.model.SolicitudCuentaEmpleado;
import com.plazoleta.restaurante.domain.spi.UsuariosRemotosPuerto;
import com.plazoleta.restaurante.infrastructure.integraciones.remoto.CuentaUsuarioRemota;
import com.plazoleta.restaurante.infrastructure.integraciones.remoto.PeticionCuentaEmpleadoRemota;
import com.plazoleta.restaurante.infrastructure.integraciones.remoto.RespuestaCuentaRemota;

import feign.FeignException;

/**
 * Adaptador de salida: cumple el puerto de usuarios remotos usando el
 * cliente Feign. Traduce los fallos HTTP del otro microservicio en las
 * excepciones de negocio de este dominio para que el flujo no filtre
 * detalles de transporte.
 */
@Component
public class AdaptadorUsuariosRemotos implements UsuariosRemotosPuerto {

    private static final String ROL_PROPIETARIO = "PROPIETARIO";

    private final ClienteUsuariosApi clienteUsuarios;

    public AdaptadorUsuariosRemotos(ClienteUsuariosApi clienteUsuarios) {
        this.clienteUsuarios = clienteUsuarios;
    }

    @Override
    public void confirmarPropietario(Long idPropietario) {
        CuentaUsuarioRemota cuenta;
        try {
            cuenta = clienteUsuarios.consultarCuenta(idPropietario);
        } catch (FeignException.NotFound cuentaAusente) {
            throw new RecursoNoEncontradoException(
                    "No hay una cuenta de usuario con el identificador " + idPropietario);
        } catch (FeignException servicioCaido) {
            throw new ReglaNegocioException(
                    "El servicio de usuarios no está disponible, intente de nuevo más tarde");
        }
        if (!ROL_PROPIETARIO.equals(cuenta.rol())) {
            throw new ReglaNegocioException(
                    "La cuenta " + idPropietario + " no tiene el rol de propietario");
        }
    }

    @Override
    public Long registrarCuentaEmpleado(SolicitudCuentaEmpleado solicitud) {
        PeticionCuentaEmpleadoRemota peticion = new PeticionCuentaEmpleadoRemota(
                solicitud.nombre(), solicitud.apellido(), solicitud.documentoIdentidad(),
                solicitud.celular(), solicitud.correo(), solicitud.clave());
        try {
            RespuestaCuentaRemota respuesta = clienteUsuarios.crearCuentaEmpleado(peticion);
            return respuesta.id();
        } catch (FeignException rechazo) {
            throw new ReglaNegocioException(
                    "El registro del empleado fue rechazado por el servicio de cuentas: "
                            + rechazo.contentUTF8());
        }
    }
}
