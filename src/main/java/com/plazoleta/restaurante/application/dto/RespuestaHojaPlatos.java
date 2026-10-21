package com.plazoleta.restaurante.application.dto;

import java.util.List;

/**
 * Hoja de resultados del menú de un restaurante (HU10).
 */
public record RespuestaHojaPlatos(
        List<RespuestaPlato> platos,
        int pagina,
        int cantidad,
        long totalElementos,
        int totalHojas) {
}
