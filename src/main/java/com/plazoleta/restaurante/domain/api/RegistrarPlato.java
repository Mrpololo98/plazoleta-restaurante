package com.plazoleta.restaurante.domain.api;

import com.plazoleta.restaurante.domain.model.Plato;

/**
 * Caso de uso HU3: agregado de un plato a la carta de un restaurante.
 */
public interface RegistrarPlato {

    /**
     * Solo el propietario dueño del restaurante puede agregar platos; el plato
     * nace habilitado con precio entero positivo.
     */
    Plato ejecutar(Plato candidato, Long idPropietario);
}
