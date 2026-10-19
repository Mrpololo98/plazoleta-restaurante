package com.plazoleta.restaurante.domain.spi;

import com.plazoleta.restaurante.domain.model.Empleado;

/**
 * Puerto de salida para la persistencia del vínculo empleado-restaurante.
 */
public interface RepositorioEmpleadosPuerto {

    Empleado guardar(Empleado empleado);
}
