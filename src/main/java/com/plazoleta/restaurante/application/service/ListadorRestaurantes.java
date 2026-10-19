package com.plazoleta.restaurante.application.service;

import org.springframework.stereotype.Service;

import com.plazoleta.restaurante.domain.api.ListarRestaurantes;
import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.model.HojaRestaurantes;
import com.plazoleta.restaurante.domain.spi.RepositorioRestaurantesPuerto;

/**
 * Implementación HU9: exploración de los restaurantes de la plazoleta.
 *
 * El listado es público, ordena alfabéticamente por nombre y permite al
 * cliente definir la cantidad de elementos por página.
 */
@Service
public class ListadorRestaurantes implements ListarRestaurantes {

    private final RepositorioRestaurantesPuerto repositorioRestaurantes;

    public ListadorRestaurantes(RepositorioRestaurantesPuerto repositorioRestaurantes) {
        this.repositorioRestaurantes = repositorioRestaurantes;
    }

    @Override
    public HojaRestaurantes ejecutar(int pagina, int cantidad) {
        if (pagina < 0) {
            throw new ReglaNegocioException("El número de página solicitado no puede ser negativo");
        }
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad de restaurantes por página debe ser mayor que cero");
        }
        return repositorioRestaurantes.listarAlfabeticamente(pagina, cantidad);
    }
}
