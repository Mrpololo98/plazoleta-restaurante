package com.plazoleta.restaurante.infrastructure.security;

/**
 * Identidad del firmante del token, reconstruida a partir de los claims del
 * JWT que emite el microservicio de usuarios.
 */
public record PortadorAutenticado(
        Long idUsuario,
        String correo,
        String rol) {
}
