package com.plazoleta.restaurante.application.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Cuerpo de la petición para corregir un plato (HU4): el precio entero
 * positivo y la descripción nueva son los únicos campos modificables.
 */
public record PeticionAjustarPlato(
        @NotNull(message = "El nuevo precio del plato es un dato obligatorio")
        @Positive(message = "El precio del plato debe ser un número entero mayor que cero")
        BigDecimal precio,
        @NotBlank(message = "La nueva descripción del plato no puede quedar vacía")
        String descripcion) {
}
