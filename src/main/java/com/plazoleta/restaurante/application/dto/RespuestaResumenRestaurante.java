package com.plazoleta.restaurante.application.dto;

/**
 * Vista mínima del restaurante dentro del listado HU9: el enunciado pide
 * mostrar únicamente el nombre y la dirección web del logo.
 */
public record RespuestaResumenRestaurante(
        String nombre,
        String urlLogo) {
}
