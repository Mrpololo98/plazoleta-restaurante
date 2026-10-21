package com.plazoleta.restaurante.application.service;

import org.springframework.stereotype.Service;

import com.plazoleta.restaurante.domain.api.ListarPlatos;
import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.restaurante.domain.model.HojaPlatos;
import com.plazoleta.restaurante.domain.model.TipoDePlato;
import com.plazoleta.restaurante.domain.spi.RepositorioPlatosPuerto;
import com.plazoleta.restaurante.domain.spi.RepositorioRestaurantesPuerto;

/**
 * Implementación HU10: exploración del menú de un restaurante.
 *
 * El listado es público, solo publica los platos habilitados (los
 * inhabilitados dejaron de ofrecerse según la HU7), ordena alfabéticamente
 * por nombre, admite filtro por categoría y deja al cliente escoger el
 * tamaño de la hoja.
 */
@Service
public class ListadorPlatos implements ListarPlatos {

    private final RepositorioPlatosPuerto repositorioPlatos;
    private final RepositorioRestaurantesPuerto repositorioRestaurantes;

    public ListadorPlatos(RepositorioPlatosPuerto repositorioPlatos,
                          RepositorioRestaurantesPuerto repositorioRestaurantes) {
        this.repositorioPlatos = repositorioPlatos;
        this.repositorioRestaurantes = repositorioRestaurantes;
    }

    @Override
    public HojaPlatos ejecutar(Long idRestaurante, TipoDePlato categoria, int pagina, int cantidad) {
        if (pagina < 0) {
            throw new ReglaNegocioException("El número de página solicitado no puede ser negativo");
        }
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad de platos por página debe ser mayor que cero");
        }
        repositorioRestaurantes.obtenerPorId(idRestaurante).orElseThrow(() ->
                new RecursoNoEncontradoException(
                        "No existe un restaurante con el identificador " + idRestaurante));
        return repositorioPlatos.listarCarta(idRestaurante, categoria, pagina, cantidad);
    }
}
