package com.plazoleta.restaurante.application.service;

import org.springframework.stereotype.Service;

import com.plazoleta.restaurante.domain.api.ConsultarRestaurante;
import com.plazoleta.restaurante.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.domain.spi.RepositorioRestaurantesPuerto;

/**
 * Implementación del caso de uso de integración ConsultarRestaurante:
 * entrega la ficha de un establecimiento para que el microservicio de
 * pedidos confirme al dueño antes de entregarle las métricas de eficiencia.
 */
@Service
public class ConsultorRestaurantes implements ConsultarRestaurante {

    private final RepositorioRestaurantesPuerto repositorioRestaurantes;

    public ConsultorRestaurantes(RepositorioRestaurantesPuerto repositorioRestaurantes) {
        this.repositorioRestaurantes = repositorioRestaurantes;
    }

    @Override
    public Restaurante ejecutar(Long id) {
        return repositorioRestaurantes.obtenerPorId(id).orElseThrow(() ->
                new RecursoNoEncontradoException(
                        "No existe un restaurante con el identificador " + id));
    }
}
