package com.plazoleta.restaurante.infrastructure.rest;

import java.time.Instant;

/**
 * Cuerpo estándar con el que responden los errores de la API.
 */
public record DetalleError(
        int estado,
        String mensaje,
        Instant ocurridoEn) {
}
