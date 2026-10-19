package com.plazoleta.restaurante.domain.model;

import java.util.List;

/**
 * Porción de resultados del listado de restaurantes (HU9).
 *
 * Tipo propio del dominio: la paginación viaja por los puertos sin exponer
 * tipos de infraestructura como Page de Spring.
 */
public record HojaRestaurantes(
        List<Restaurante> contenido,
        int pagina,
        int cantidad,
        long totalElementos) {

    /**
     * Cantidad de hojas que produce el total de elementos con el tamaño dado.
     */
    public int totalHojas() {
        return cantidad <= 0 ? 0 : (int) Math.ceil((double) totalElementos / cantidad);
    }
}
