package com.plazoleta.restaurante.application.dto;

/**
 * Vista del vínculo laboral creado (HU6): el identificador de la cuenta que
 * el microservicio de usuarios asignó al empleado y su restaurante.
 */
public record RespuestaEmpleado(
        Long id,
        Long idUsuario,
        Long idRestaurante) {
}
