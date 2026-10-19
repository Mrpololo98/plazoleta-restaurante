package com.plazoleta.restaurante.application.service;

import org.springframework.stereotype.Service;

import com.plazoleta.restaurante.domain.api.AlternarPlato;
import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.spi.RepositorioPlatosPuerto;

/**
 * Implementación HU7: habilitar o inhabilitar un plato de la carta.
 *
 * Del enunciado: solo el dueño del restaurante asociado al plato puede
 * cambiar la disponibilidad y ningún otro dato del plato se toca.
 */
@Service
public class AlternadorPlatos implements AlternarPlato {

    private final RepositorioPlatosPuerto repositorioPlatos;
    private final VerificadorPropiedadRestaurante verificadorPropiedad;

    public AlternadorPlatos(RepositorioPlatosPuerto repositorioPlatos,
                            VerificadorPropiedadRestaurante verificadorPropiedad) {
        this.repositorioPlatos = repositorioPlatos;
        this.verificadorPropiedad = verificadorPropiedad;
    }

    @Override
    public Plato ejecutar(Long idPlato, Boolean activo, Long idPropietario) {
        if (idPlato == null || idPlato <= 0) {
            throw new ReglaNegocioException("El identificador del plato debe ser un número positivo");
        }
        if (idPropietario == null || idPropietario <= 0) {
            throw new ReglaNegocioException("El token no trae el identificador del propietario");
        }
        if (activo == null) {
            throw new ReglaNegocioException("La nueva disponibilidad del plato es un dato obligatorio");
        }

        Plato plato = repositorioPlatos.obtenerPorId(idPlato)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un plato registrado con el identificador " + idPlato));

        verificadorPropiedad.exigirDueño(plato.idRestaurante(), idPropietario);

        // HU7: únicamente cambia la disponibilidad; el resto de la ficha
        // se copia tal cual a la instancia nueva.
        Plato conNuevoEstado = new Plato(plato.id(), plato.nombre(), plato.categoria(),
                plato.descripcion(), plato.precio(), plato.urlImagen(),
                plato.idRestaurante(), activo);
        return repositorioPlatos.guardar(conNuevoEstado);
    }
}
