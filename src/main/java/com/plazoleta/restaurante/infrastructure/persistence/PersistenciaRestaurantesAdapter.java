package com.plazoleta.restaurante.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.plazoleta.restaurante.domain.model.HojaRestaurantes;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.domain.spi.RepositorioRestaurantesPuerto;

/**
 * Adaptador de salida: implementa el puerto de restaurantes sobre JPA.
 * La base de datos resuelve el orden alfabético del listado HU9.
 */
@Component
public class PersistenciaRestaurantesAdapter implements RepositorioRestaurantesPuerto {

    private final RepositorioRestaurantesJpa repositorioJpa;
    private final ConversorRestaurantesPersistencia conversor;

    public PersistenciaRestaurantesAdapter(RepositorioRestaurantesJpa repositorioJpa,
                                            ConversorRestaurantesPersistencia conversor) {
        this.repositorioJpa = repositorioJpa;
        this.conversor = conversor;
    }

    @Override
    public Restaurante guardar(Restaurante restaurante) {
        RestauranteEntidad entidad = repositorioJpa.save(conversor.aEntidad(restaurante));
        return conversor.aDominio(entidad);
    }

    @Override
    public Optional<Restaurante> obtenerPorId(Long id) {
        return repositorioJpa.findById(id).map(conversor::aDominio);
    }

    @Override
    public HojaRestaurantes listarAlfabeticamente(int pagina, int cantidad) {
        var hoja = repositorioJpa.findAll(
                PageRequest.of(pagina, cantidad, Sort.by(Sort.Direction.ASC, "nombre")));
        List<Restaurante> restaurantes = hoja.getContent().stream()
                .map(conversor::aDominio)
                .toList();
        return new HojaRestaurantes(restaurantes, pagina, cantidad, hoja.getTotalElements());
    }
}
