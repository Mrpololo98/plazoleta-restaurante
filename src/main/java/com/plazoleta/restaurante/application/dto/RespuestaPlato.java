package com.plazoleta.restaurante.application.dto;

import java.math.BigDecimal;

import com.plazoleta.restaurante.domain.model.TipoDePlato;

/**
 * Vista de un plato creada o actualizada (HU3, HU4 y HU7).
 */
public record RespuestaPlato(
        Long id,
        String nombre,
        TipoDePlato categoria,
        String descripcion,
        BigDecimal precio,
        String urlImagen,
        Long idRestaurante,
        Boolean activo) {
}
