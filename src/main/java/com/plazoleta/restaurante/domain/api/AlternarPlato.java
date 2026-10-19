package com.plazoleta.restaurante.domain.api;

import com.plazoleta.restaurante.domain.model.Plato;

/**
 * Caso de uso HU7: habilitar o inhabilitar un plato de la carta.
 */
public interface AlternarPlato {

    /**
     * Cambia la disponibilidad de un plato propio sin tocar ningún otro dato.
     */
    Plato ejecutar(Long idPlato, Boolean activo, Long idPropietario);
}
