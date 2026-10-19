package com.plazoleta.restaurante.domain.exception;

/**
 * Señala el rompimiento de una regla de negocio de la plazoleta: datos
 * inválidos o una operación que no corresponde al solicitante.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
