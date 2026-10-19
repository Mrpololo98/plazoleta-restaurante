package com.plazoleta.restaurante.domain.spi;

import com.plazoleta.restaurante.domain.model.SolicitudCuentaEmpleado;

/**
 * Puerto de salida hacia el microservicio de usuarios (comunicación entre
 * microservicios por Feign).
 */
public interface UsuariosRemotosPuerto {

    /**
     * Confirma que el identificador corresponde a una cuenta activa con rol
     * PROPIETARIO; falla con 404 si la cuenta no existe o con 400 si el rol
     * no es el esperado.
     */
    void confirmarPropietario(Long idPropietario);

    /**
     * Pide al microservicio de usuarios la creación de la cuenta del empleado
     * y devuelve el identificador que le asignó.
     */
    Long registrarCuentaEmpleado(SolicitudCuentaEmpleado solicitud);
}
