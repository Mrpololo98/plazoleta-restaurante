package com.plazoleta.restaurante.domain.spi;

import java.util.Optional;

import com.plazoleta.restaurante.domain.model.HojaRestaurantes;
import com.plazoleta.restaurante.domain.model.Restaurante;

/**
 * Puerto de salida para la persistencia de restaurantes.
 */
public interface RepositorioRestaurantesPuerto {

    Restaurante guardar(Restaurante restaurante);

    Optional<Restaurante> obtenerPorId(Long id);

    /**
     * Listado ordenado alfabéticamente por nombre (HU9); la base de datos
     * resuelve el orden y la paginación.
     */
    HojaRestaurantes listarAlfabeticamente(int pagina, int cantidad);
}
