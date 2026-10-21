package com.plazoleta.restaurante.domain.api;

import com.plazoleta.restaurante.domain.model.Empleado;

/**
 * Caso de uso de integración: consulta el vínculo laboral de una cuenta de
 * empleado.
 *
 * Lo usa el microservicio de pedidos para saber a qué restaurante pertenece
 * un empleado antes de mostrarle pedidos (HU12) o dejarle cambiar su estado.
 */
public interface ConsultarEmpleado {

    Empleado ejecutar(Long idUsuario);
}
