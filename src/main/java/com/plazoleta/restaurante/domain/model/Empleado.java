package com.plazoleta.restaurante.domain.model;

/**
 * Vínculo de trabajo entre una cuenta de empleado y un restaurante de la
 * plazoleta (HU6). Los datos personales del empleado viven en el
 * microservicio de usuarios; aquí solo queda la relación laboral.
 */
public record Empleado(
        Long id,
        Long idUsuario,
        Long idRestaurante) {
}
