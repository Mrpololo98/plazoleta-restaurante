package com.plazoleta.restaurante.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Cuerpo de la petición para contratar un empleado (HU6): además de los
 * datos de la cuenta viaja el restaurante donde va a trabajar.
 */
public record PeticionVincularEmpleado(
        @NotNull(message = "El identificador del restaurante es obligatorio")
        @Positive(message = "El identificador del restaurante debe ser mayor que cero")
        Long idRestaurante,
        @NotBlank(message = "El nombre del empleado no puede quedar vacío")
        String nombre,
        @NotBlank(message = "El apellido del empleado no puede quedar vacío")
        String apellido,
        @NotBlank(message = "El documento de identidad no puede quedar vacío")
        String documentoIdentidad,
        @NotBlank(message = "El celular no puede quedar vacío")
        String celular,
        @NotBlank(message = "El correo no puede quedar vacío")
        String correo,
        @NotBlank(message = "La clave no puede quedar vacía")
        String clave) {
}
