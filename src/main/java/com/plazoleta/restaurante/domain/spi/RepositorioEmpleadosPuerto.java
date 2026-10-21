package com.plazoleta.restaurante.domain.spi;

import java.util.Optional;

import com.plazoleta.restaurante.domain.model.Empleado;

/**
 * Puerto de salida para la persistencia del vínculo empleado-restaurante.
 */
public interface RepositorioEmpleadosPuerto {

    Empleado guardar(Empleado empleado);

    /**
     * Busca el vínculo laboral de una cuenta de empleado; lo consulta el
     * microservicio de pedidos para filtrar sus órdenes (HU12).
     */
    Optional<Empleado> obtenerPorIdUsuario(Long idUsuario);
}
