package com.plazoleta.restaurante.domain.model;

import java.math.BigDecimal;

/**
 * Modelo de dominio de un plato de la carta de un restaurante.
 *
 * Es un portador de datos puro e inmutable: los casos de uso de la HU4
 * (ajuste de precio y descripción) y de la HU7 (disponibilidad) construyen
 * una instancia nueva con los datos actualizados en lugar de mutar la
 * original, así el dominio nunca queda a medias si una validación posterior
 * falla.
 */
public record Plato(
        Long id,
        String nombre,
        TipoDePlato categoria,
        String descripcion,
        BigDecimal precio,
        String urlImagen,
        Long idRestaurante,
        Boolean activo) {
}
