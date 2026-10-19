package com.plazoleta.restaurante.infrastructure.rest;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.plazoleta.restaurante.application.dto.PeticionRegistrarRestaurante;
import com.plazoleta.restaurante.application.dto.RespuestaHojaRestaurantes;
import com.plazoleta.restaurante.application.dto.RespuestaResumenRestaurante;
import com.plazoleta.restaurante.application.dto.RespuestaRestaurante;
import com.plazoleta.restaurante.domain.model.HojaRestaurantes;
import com.plazoleta.restaurante.domain.model.Restaurante;

/**
 * Traduce entre los DTO de entrada/salida HTTP y el modelo de dominio de
 * restaurantes, incluida la hoja del listado HU9.
 */
@Mapper(componentModel = "spring")
public interface ConversorRestaurantesRest {

    /**
     * Convierte la petición HTTP en un candidato del dominio; el
     * identificador lo genera la base de datos al guardar.
     */
    @Mapping(target = "id", ignore = true)
    Restaurante aDominio(PeticionRegistrarRestaurante peticion);

    RespuestaRestaurante aRespuesta(Restaurante restaurante);

    RespuestaResumenRestaurante aResumen(Restaurante restaurante);

    /**
     * Arma la respuesta paginada del listado HU9: cada restaurante queda
     * reducido al nombre y al logo.
     */
    default RespuestaHojaRestaurantes aRespuestaHoja(HojaRestaurantes hoja) {
        List<RespuestaResumenRestaurante> restaurantes = hoja.contenido().stream()
                .map(this::aResumen)
                .toList();
        return new RespuestaHojaRestaurantes(restaurantes, hoja.pagina(), hoja.cantidad(),
                hoja.totalElementos(), hoja.totalHojas());
    }
}
