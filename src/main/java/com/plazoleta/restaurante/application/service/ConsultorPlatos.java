package com.plazoleta.restaurante.application.service;

import org.springframework.stereotype.Service;

import com.plazoleta.restaurante.domain.api.ConsultarPlato;
import com.plazoleta.restaurante.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.spi.RepositorioPlatosPuerto;

/**
 * Implementación del caso de uso de integración ConsultarPlato: entrega la
 * ficha de un plato para que otros microservicios (pedidos) validen sus
 * órdenes sin duplicar la carta.
 */
@Service
public class ConsultorPlatos implements ConsultarPlato {

    private final RepositorioPlatosPuerto repositorioPlatos;

    public ConsultorPlatos(RepositorioPlatosPuerto repositorioPlatos) {
        this.repositorioPlatos = repositorioPlatos;
    }

    @Override
    public Plato ejecutar(Long id) {
        return repositorioPlatos.obtenerPorId(id).orElseThrow(() ->
                new RecursoNoEncontradoException("No existe un plato con el identificador " + id));
    }
}
