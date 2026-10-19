package com.plazoleta.restaurante.domain.api;

import java.math.BigDecimal;

import com.plazoleta.restaurante.domain.model.Plato;

/**
 * Caso de uso HU4: corrección de precio y descripción de un plato.
 */
public interface AjustarPlato {

    /**
     * Actualiza únicamente el precio y la descripción de un plato propio.
     */
    Plato ejecutar(Long idPlato, BigDecimal precio, String descripcion, Long idPropietario);
}
