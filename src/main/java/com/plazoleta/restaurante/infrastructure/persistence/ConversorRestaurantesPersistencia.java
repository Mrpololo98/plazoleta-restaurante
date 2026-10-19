package com.plazoleta.restaurante.infrastructure.persistence;

import org.mapstruct.Mapper;

import com.plazoleta.restaurante.domain.model.Restaurante;

/**
 * Traduce entre el modelo de dominio y la entidad JPA de restaurantes.
 */
@Mapper(componentModel = "spring")
public interface ConversorRestaurantesPersistencia {

    RestauranteEntidad aEntidad(Restaurante restaurante);

    Restaurante aDominio(RestauranteEntidad entidad);
}
