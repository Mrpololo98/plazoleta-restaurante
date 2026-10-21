package com.plazoleta.restaurante.domain.spi;

import java.util.Optional;

import com.plazoleta.restaurante.domain.model.HojaPlatos;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.model.TipoDePlato;

/**
 * Puerto de salida para la persistencia de platos.
 */
public interface RepositorioPlatosPuerto {

    Plato guardar(Plato plato);

    Optional<Plato> obtenerPorId(Long id);

    /**
     * Carta de un restaurante (HU10): solo platos habilitados, ordenados
     * alfabéticamente y por hojas; la categoría es un filtro opcional.
     */
    HojaPlatos listarCarta(Long idRestaurante, TipoDePlato categoria, int pagina, int cantidad);
}
