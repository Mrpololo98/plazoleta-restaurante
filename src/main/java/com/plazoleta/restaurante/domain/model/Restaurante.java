package com.plazoleta.restaurante.domain.model;

/**
 * Modelo de dominio de un restaurante de la plazoleta.
 * Capa pura: no conoce Spring, JPA ni detalles del transporte.
 */
public record Restaurante(
        Long id,
        String nombre,
        String nit,
        String direccion,
        String telefono,
        String urlLogo,
        Long idPropietario) {
}
