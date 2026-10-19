package com.plazoleta.restaurante.application.dto;

import java.math.BigDecimal;

import com.plazoleta.restaurante.domain.model.TipoDePlato;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Cuerpo de la petición para agregar un plato a la carta (HU3).
 */
public record PeticionRegistrarPlato(
        @NotBlank(message = "El nombre del plato no puede quedar vacío")
        String nombre,
        @NotNull(message = "La categoría del plato es un dato obligatorio")
        TipoDePlato categoria,
        @NotNull(message = "El precio del plato es un dato obligatorio")
        @Positive(message = "El precio del plato debe ser un número entero mayor que cero")
        BigDecimal precio,
        @NotBlank(message = "La descripción del plato no puede quedar vacía")
        String descripcion,
        @NotBlank(message = "La dirección web de la imagen del plato no puede quedar vacía")
        String urlImagen,
        @NotNull(message = "El identificador del restaurante es obligatorio")
        @Positive(message = "El identificador del restaurante debe ser mayor que cero")
        Long idRestaurante) {
}
