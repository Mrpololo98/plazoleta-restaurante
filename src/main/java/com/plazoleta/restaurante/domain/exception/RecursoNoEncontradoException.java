package com.plazoleta.restaurante.domain.exception;

/**
 * Señala que el recurso solicitado (restaurante, plato o cuenta de
 * usuario) no existe en el sistema.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
