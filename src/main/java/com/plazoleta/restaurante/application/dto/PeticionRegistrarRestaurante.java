package com.plazoleta.restaurante.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de la petición para registrar un restaurante (HU2).
 */
public record PeticionRegistrarRestaurante(
        @NotBlank(message = "El nombre del restaurante no puede quedar vacío")
        String nombre,
        @NotBlank(message = "El NIT del restaurante no puede quedar vacío")
        @Pattern(regexp = "^\\d+$", message = "El NIT del restaurante solo admite dígitos")
        String nit,
        @NotBlank(message = "La dirección del restaurante no puede quedar vacía")
        String direccion,
        @NotBlank(message = "El teléfono del restaurante no puede quedar vacío")
        @Pattern(regexp = "^\\+?\\d+$", message = "El teléfono solo admite dígitos y un '+' inicial")
        @Size(max = 13, message = "El teléfono no puede superar los 13 caracteres")
        String telefono,
        @NotBlank(message = "La dirección web del logo no puede quedar vacía")
        String urlLogo,
        @NotNull(message = "El identificador del propietario es obligatorio")
        @Positive(message = "El identificador del propietario debe ser mayor que cero")
        Long idPropietario) {
}
