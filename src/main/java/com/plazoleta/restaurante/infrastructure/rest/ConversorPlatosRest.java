package com.plazoleta.restaurante.infrastructure.rest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.plazoleta.restaurante.application.dto.PeticionRegistrarPlato;
import com.plazoleta.restaurante.application.dto.RespuestaPlato;
import com.plazoleta.restaurante.domain.model.Plato;

/**
 * Traduce entre los DTO de entrada/salida HTTP y el modelo de dominio de
 * platos.
 */
@Mapper(componentModel = "spring")
public interface ConversorPlatosRest {

    /**
     * Convierte la petición HTTP en un candidato del dominio; el
     * identificador lo pone la base y la disponibilidad la fija el caso
     * de uso (nace habilitado).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    Plato aDominio(PeticionRegistrarPlato peticion);

    RespuestaPlato aRespuesta(Plato plato);
}
