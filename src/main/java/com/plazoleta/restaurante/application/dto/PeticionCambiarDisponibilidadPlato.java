package com.plazoleta.restaurante.application.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de la petición para habilitar o inhabilitar un plato (HU7).
 */
public record PeticionCambiarDisponibilidadPlato(
        @NotNull(message = "La nueva disponibilidad del plato es un dato obligatorio")
        Boolean activo) {
}
