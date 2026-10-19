package com.plazoleta.restaurante.application.dto;

/**
 * Vista completa de un restaurante recién creado o consultado (HU2).
 */
public record RespuestaRestaurante(
        Long id,
        String nombre,
        String nit,
        String direccion,
        String telefono,
        String urlLogo,
        Long idPropietario) {
}
