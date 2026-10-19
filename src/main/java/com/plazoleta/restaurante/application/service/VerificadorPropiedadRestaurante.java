package com.plazoleta.restaurante.application.service;

import org.springframework.stereotype.Component;

import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.domain.spi.RepositorioRestaurantesPuerto;
import com.plazoleta.restaurante.domain.spi.UsuariosRemotosPuerto;

/**
 * Regla compartida por los casos de uso de platos (HU3, HU4, HU7) y por la
 * contratación de empleados (HU6): el restaurante debe existir, pertenecer al
 * propietario que firma la petición y ese propietario debe seguir vigente
 * como PROPIETARIO en el microservicio de usuarios.
 */
@Component
public class VerificadorPropiedadRestaurante {

    private final RepositorioRestaurantesPuerto repositorioRestaurantes;
    private final UsuariosRemotosPuerto usuariosRemotos;

    public VerificadorPropiedadRestaurante(RepositorioRestaurantesPuerto repositorioRestaurantes,
                                           UsuariosRemotosPuerto usuariosRemotos) {
        this.repositorioRestaurantes = repositorioRestaurantes;
        this.usuariosRemotos = usuariosRemotos;
    }

    /**
     * Devuelve el restaurante buscado cuando pertenece al propietario dado;
     * en caso contrario lanza 404 si no existe o 400 si el dueño es otro.
     */
    public Restaurante exigirDueño(Long idRestaurante, Long idPropietario) {
        Restaurante restaurante = repositorioRestaurantes.obtenerPorId(idRestaurante)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un restaurante registrado con el identificador " + idRestaurante));

        if (!restaurante.idPropietario().equals(idPropietario)) {
            throw new ReglaNegocioException("El restaurante con identificador " + idRestaurante
                    + " no pertenece al propietario autenticado");
        }

        usuariosRemotos.confirmarPropietario(idPropietario);
        return restaurante;
    }
}
