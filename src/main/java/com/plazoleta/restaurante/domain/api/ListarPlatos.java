package com.plazoleta.restaurante.domain.api;

import com.plazoleta.restaurante.domain.model.HojaPlatos;
import com.plazoleta.restaurante.domain.model.TipoDePlato;

/**
 * Caso de uso HU10: listar la carta de un restaurante.
 *
 * El menú solo publica los platos habilitados, va ordenado alfabéticamente
 * por nombre y se puede filtrar por categoría. El cliente decide cuántos
 * elementos trae cada hoja.
 */
public interface ListarPlatos {

    HojaPlatos ejecutar(Long idRestaurante, TipoDePlato categoria, int pagina, int cantidad);
}
