package com.plazoleta.restaurante.application.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.plazoleta.restaurante.domain.api.AjustarPlato;
import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.spi.RepositorioPlatosPuerto;

/**
 * Implementación HU4: corrección de precio y descripción de un plato.
 *
 * Del enunciado: solo el dueño del restaurante asociado al plato puede
 * ajustarlo y únicamente cambian el precio (entero positivo) y la
 * descripción; el resto de la ficha queda intacto.
 */
@Service
public class AjustadorPlatos implements AjustarPlato {

    private final RepositorioPlatosPuerto repositorioPlatos;
    private final VerificadorPropiedadRestaurante verificadorPropiedad;

    public AjustadorPlatos(RepositorioPlatosPuerto repositorioPlatos,
                           VerificadorPropiedadRestaurante verificadorPropiedad) {
        this.repositorioPlatos = repositorioPlatos;
        this.verificadorPropiedad = verificadorPropiedad;
    }

    @Override
    public Plato ejecutar(Long idPlato, BigDecimal precio, String descripcion, Long idPropietario) {
        if (idPlato == null || idPlato <= 0) {
            throw new ReglaNegocioException("El identificador del plato debe ser un número positivo");
        }
        if (idPropietario == null || idPropietario <= 0) {
            throw new ReglaNegocioException("El token no trae el identificador del propietario");
        }
        if (!RegistradorPlatos.esPrecioEnteroPositivo(precio)) {
            throw new ReglaNegocioException("El precio del plato debe ser un número entero mayor que cero");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new ReglaNegocioException("La nueva descripción del plato no puede quedar vacía");
        }

        Plato plato = repositorioPlatos.obtenerPorId(idPlato)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un plato registrado con el identificador " + idPlato));

        verificadorPropiedad.exigirDueño(plato.idRestaurante(), idPropietario);

        // HU4: únicamente cambian el precio y la descripción; el resto de la
        // ficha se copia tal cual a la instancia nueva.
        Plato corregido = new Plato(plato.id(), plato.nombre(), plato.categoria(),
                descripcion, precio, plato.urlImagen(), plato.idRestaurante(),
                plato.activo());
        return repositorioPlatos.guardar(corregido);
    }
}
