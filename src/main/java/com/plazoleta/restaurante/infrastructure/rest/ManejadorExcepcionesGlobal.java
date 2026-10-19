package com.plazoleta.restaurante.infrastructure.rest;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.exception.RecursoNoEncontradoException;

/**
 * Convierte las excepciones del dominio en respuestas HTTP legibles y
 * uniformes para quien consume la API.
 */
@RestControllerAdvice
public class ManejadorExcepcionesGlobal {

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<DetalleError> manejarReglaNegocio(ReglaNegocioException excepcion) {
        return construir(HttpStatus.BAD_REQUEST, excepcion.getMessage());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<DetalleError> manejarRecursoAusente(RecursoNoEncontradoException excepcion) {
        return construir(HttpStatus.NOT_FOUND, excepcion.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DetalleError> manejarValidacionDeEntrada(MethodArgumentNotValidException excepcion) {
        String detalles = excepcion.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(" | "));
        return construir(HttpStatus.BAD_REQUEST, detalles);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DetalleError> manejarErroresInesperados(Exception excepcion) {
        return construir(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado, por favor intente más tarde");
    }

    private ResponseEntity<DetalleError> construir(HttpStatus estado, String mensaje) {
        DetalleError detalle = new DetalleError(estado.value(), mensaje, Instant.now());
        return ResponseEntity.status(estado).body(detalle);
    }
}
