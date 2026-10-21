package com.plazoleta.restaurante.domain.model;

import java.util.List;

/**
 * Porción de resultados de la carta de un restaurante (HU10).
 *
 * Tipo propio del dominio: la paginación viaja por los puertos sin exponer
 * tipos de infraestructura como Page de Spring.
 */
public record HojaPlatos(
        List<Plato> contenido,
        int pagina,
        int cantidad,
        long totalElementos) {

    /**
     * Cantidad de hojas que produce el total de platos con el tamaño dado.
     */
    public int totalHojas() {
        return cantidad <= 0 ? 0 : (int) Math.ceil((double) totalElementos / cantidad);
    }
}
