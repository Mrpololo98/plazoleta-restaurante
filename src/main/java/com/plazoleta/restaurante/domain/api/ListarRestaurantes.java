package com.plazoleta.restaurante.domain.api;

import com.plazoleta.restaurante.domain.model.HojaRestaurantes;

/**
 * Caso de uso HU9: exploración de los restaurantes de la plazoleta.
 */
public interface ListarRestaurantes {

    /**
     * Devuelve una hoja de restaurantes ordenados alfabéticamente por nombre.
     */
    HojaRestaurantes ejecutar(int pagina, int cantidad);
}
