package com.plazoleta.restaurante.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.spi.RepositorioPlatosPuerto;

/**
 * Adaptador de salida: implementa el puerto de platos sobre JPA.
 */
@Component
public class PersistenciaPlatosAdapter implements RepositorioPlatosPuerto {

    private final RepositorioPlatosJpa repositorioJpa;
    private final ConversorPlatosPersistencia conversor;

    public PersistenciaPlatosAdapter(RepositorioPlatosJpa repositorioJpa,
                                     ConversorPlatosPersistencia conversor) {
        this.repositorioJpa = repositorioJpa;
        this.conversor = conversor;
    }

    @Override
    public Plato guardar(Plato plato) {
        PlatoEntidad entidad = repositorioJpa.save(conversor.aEntidad(plato));
        return conversor.aDominio(entidad);
    }

    @Override
    public Optional<Plato> obtenerPorId(Long id) {
        return repositorioJpa.findById(id).map(conversor::aDominio);
    }
}
