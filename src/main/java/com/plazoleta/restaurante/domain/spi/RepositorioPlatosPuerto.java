package com.plazoleta.restaurante.domain.spi;

import java.util.Optional;

import com.plazoleta.restaurante.domain.model.Plato;

/**
 * Puerto de salida para la persistencia de platos.
 */
public interface RepositorioPlatosPuerto {

    Plato guardar(Plato plato);

    Optional<Plato> obtenerPorId(Long id);
}
