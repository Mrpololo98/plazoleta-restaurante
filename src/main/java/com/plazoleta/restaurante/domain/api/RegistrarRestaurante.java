package com.plazoleta.restaurante.domain.api;

import com.plazoleta.restaurante.domain.model.Restaurante;

/**
 * Caso de uso HU2: registro de un restaurante por parte del administrador.
 */
public interface RegistrarRestaurante {

    /**
     * Valida los datos del establecimiento, confirma con el microservicio de
     * usuarios que el dueño indicado tiene rol PROPIETARIO y lo persiste.
     */
    Restaurante ejecutar(Restaurante candidato);
}
