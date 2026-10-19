package com.plazoleta.restaurante.domain.api;

import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.domain.model.SolicitudCuentaEmpleado;

/**
 * Caso de uso HU6: contratación de un empleado para un restaurante.
 */
public interface VincularEmpleado {

    /**
     * Crea la cuenta del empleado en el microservicio de usuarios y guarda el
     * vínculo laboral con el restaurante, siempre que quien pida sea el dueño.
     */
    Empleado ejecutar(SolicitudCuentaEmpleado solicitud, Long idRestaurante, Long idPropietario);
}
