package com.plazoleta.restaurante.infrastructure.rest;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.plazoleta.restaurante.application.dto.PeticionRegistrarPlato;
import com.plazoleta.restaurante.application.dto.RespuestaHojaPlatos;
import com.plazoleta.restaurante.application.dto.RespuestaPlato;
import com.plazoleta.restaurante.domain.model.HojaPlatos;
import com.plazoleta.restaurante.domain.model.Plato;

/**
 * Traduce entre los DTO de entrada/salida HTTP y el modelo de dominio de
 * platos, incluida la hoja del menú HU10.
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

    /**
     * Arma la respuesta paginada del menú HU10 con la ficha completa de
     * cada plato habilitado.
     */
    default RespuestaHojaPlatos aRespuestaHoja(HojaPlatos hoja) {
        List<RespuestaPlato> platos = hoja.contenido().stream()
                .map(this::aRespuesta)
                .toList();
        return new RespuestaHojaPlatos(platos, hoja.pagina(), hoja.cantidad(),
                hoja.totalElementos(), hoja.totalHojas());
    }
}
