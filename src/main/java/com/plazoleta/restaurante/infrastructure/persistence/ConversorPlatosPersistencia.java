package com.plazoleta.restaurante.infrastructure.persistence;

import org.mapstruct.Mapper;

import com.plazoleta.restaurante.domain.model.Plato;

/**
 * Traduce entre el modelo de dominio y la entidad JPA de platos.
 */
@Mapper(componentModel = "spring")
public interface ConversorPlatosPersistencia {

    PlatoEntidad aEntidad(Plato plato);

    Plato aDominio(PlatoEntidad entidad);
}
