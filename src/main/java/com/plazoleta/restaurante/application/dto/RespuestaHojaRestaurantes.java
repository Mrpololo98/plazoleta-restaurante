package com.plazoleta.restaurante.application.dto;

import java.util.List;

/**
 * Hoja de resultados del listado de restaurantes (HU9).
 */
public record RespuestaHojaRestaurantes(
        List<RespuestaResumenRestaurante> restaurantes,
        int pagina,
        int cantidad,
        long totalElementos,
        int totalHojas) {
}
