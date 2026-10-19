package com.plazoleta.restaurante.application.service;

import org.springframework.stereotype.Service;

import com.plazoleta.restaurante.domain.api.VincularEmpleado;
import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.domain.model.SolicitudCuentaEmpleado;
import com.plazoleta.restaurante.domain.spi.RepositorioEmpleadosPuerto;
import com.plazoleta.restaurante.domain.spi.UsuariosRemotosPuerto;

/**
 * Implementación HU6: contratación de empleados para un restaurante.
 *
 * El flujo respeta la separación de responsabilidades entre microservicios:
 * este confirma que quien pide sea el dueño del restaurante, le pide al
 * microservicio de usuarios que cree la cuenta (con rol EMPLEADO y la clave
 * protegida con BCrypt) y aquí solo guarda el vínculo laboral.
 */
@Service
public class VinculadorEmpleados implements VincularEmpleado {

    private final VerificadorPropiedadRestaurante verificadorPropiedad;
    private final UsuariosRemotosPuerto usuariosRemotos;
    private final RepositorioEmpleadosPuerto repositorioEmpleados;

    public VinculadorEmpleados(VerificadorPropiedadRestaurante verificadorPropiedad,
                               UsuariosRemotosPuerto usuariosRemotos,
                               RepositorioEmpleadosPuerto repositorioEmpleados) {
        this.verificadorPropiedad = verificadorPropiedad;
        this.usuariosRemotos = usuariosRemotos;
        this.repositorioEmpleados = repositorioEmpleados;
    }

    @Override
    public Empleado ejecutar(SolicitudCuentaEmpleado solicitud,
                             Long idRestaurante,
                             Long idPropietario) {
        if (solicitud == null) {
            throw new ReglaNegocioException("El cuerpo de la petición es obligatorio");
        }
        if (idRestaurante == null || idRestaurante <= 0) {
            throw new ReglaNegocioException("Debe indicarse el restaurante donde trabajará el empleado");
        }
        if (idPropietario == null || idPropietario <= 0) {
            throw new ReglaNegocioException("El token no trae el identificador del propietario");
        }
        revisarDatosDeLaCuenta(solicitud);

        verificadorPropiedad.exigirDueño(idRestaurante, idPropietario);

        Long idUsuario = usuariosRemotos.registrarCuentaEmpleado(solicitud);

        return repositorioEmpleados.guardar(new Empleado(null, idUsuario, idRestaurante));
    }

    private void revisarDatosDeLaCuenta(SolicitudCuentaEmpleado solicitud) {
        exigirPresente(solicitud.nombre(), "nombre del empleado");
        exigirPresente(solicitud.apellido(), "apellido del empleado");
        exigirPresente(solicitud.documentoIdentidad(), "documento de identidad del empleado");
        exigirPresente(solicitud.celular(), "celular del empleado");
        exigirPresente(solicitud.correo(), "correo del empleado");
        exigirPresente(solicitud.clave(), "clave del empleado");
    }

    private void exigirPresente(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("El campo " + campo + " no puede quedar vacío");
        }
    }
}
