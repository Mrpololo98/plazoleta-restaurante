package com.plazoleta.restaurante.infrastructure.rest;

import org.springframework.stereotype.Component;

import com.plazoleta.restaurante.application.dto.PeticionVincularEmpleado;
import com.plazoleta.restaurante.application.dto.RespuestaEmpleado;
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.domain.model.SolicitudCuentaEmpleado;

/**
 * Traduce la petición de contratación en la solicitud de cuenta que viaja
 * al microservicio de usuarios, y el vínculo guardado en la respuesta HTTP.
 * Al tener tres campos, el mapeo se hace a mano.
 */
@Component
public class ConversorEmpleadosRest {

    public SolicitudCuentaEmpleado aSolicitud(PeticionVincularEmpleado peticion) {
        return new SolicitudCuentaEmpleado(
                peticion.nombre(),
                peticion.apellido(),
                peticion.documentoIdentidad(),
                peticion.celular(),
                peticion.correo(),
                peticion.clave());
    }

    public RespuestaEmpleado aRespuesta(Empleado empleado) {
        return new RespuestaEmpleado(empleado.id(), empleado.idUsuario(), empleado.idRestaurante());
    }
}
