package com.plazoleta.restaurante.application.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.plazoleta.restaurante.domain.api.RegistrarPlato;
import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.spi.RepositorioPlatosPuerto;

/**
 * Implementación HU3: agregado de platos a la carta de un restaurante.
 *
 * Exige los campos del enunciado, valida que el precio sea un entero
 * positivo, confirma que quien pide sea el dueño del restaurante y deja
 * el plato naciendo habilitado (activo).
 */
@Service
public class RegistradorPlatos implements RegistrarPlato {

    private final RepositorioPlatosPuerto repositorioPlatos;
    private final VerificadorPropiedadRestaurante verificadorPropiedad;

    public RegistradorPlatos(RepositorioPlatosPuerto repositorioPlatos,
                             VerificadorPropiedadRestaurante verificadorPropiedad) {
        this.repositorioPlatos = repositorioPlatos;
        this.verificadorPropiedad = verificadorPropiedad;
    }

    @Override
    public Plato ejecutar(Plato candidato, Long idPropietario) {
        revisarDatos(candidato, idPropietario);

        verificadorPropiedad.exigirDueño(candidato.idRestaurante(), idPropietario);

        // HU3: el plato nace habilitado en la carta.
        Plato nuevo = new Plato(null, candidato.nombre(), candidato.categoria(),
                candidato.descripcion(), candidato.precio(), candidato.urlImagen(),
                candidato.idRestaurante(), true);
        return repositorioPlatos.guardar(nuevo);
    }

    private void revisarDatos(Plato candidato, Long idPropietario) {
        if (candidato == null) {
            throw new ReglaNegocioException("El cuerpo de la petición es obligatorio");
        }
        exigirPresente(candidato.nombre(), "nombre del plato");
        if (candidato.categoria() == null) {
            throw new ReglaNegocioException("La categoría del plato es un dato obligatorio");
        }
        exigirPresente(candidato.descripcion(), "descripción del plato");
        exigirPresente(candidato.urlImagen(), "dirección web de la imagen del plato");
        if (candidato.idRestaurante() == null || candidato.idRestaurante() <= 0) {
            throw new ReglaNegocioException("Debe indicarse el restaurante al que pertenece el plato");
        }
        if (idPropietario == null || idPropietario <= 0) {
            throw new ReglaNegocioException("El token no trae el identificador del propietario");
        }
        if (!esPrecioEnteroPositivo(candidato.precio())) {
            throw new ReglaNegocioException("El precio del plato debe ser un número entero mayor que cero");
        }
    }

    /**
     * Revisión compartida del precio: mayor que cero y sin decimales.
     */
    static boolean esPrecioEnteroPositivo(BigDecimal precio) {
        return precio != null
                && precio.compareTo(BigDecimal.ZERO) > 0
                && precio.stripTrailingZeros().scale() <= 0;
    }

    private void exigirPresente(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("El campo " + campo + " no puede quedar vacío");
        }
    }
}
