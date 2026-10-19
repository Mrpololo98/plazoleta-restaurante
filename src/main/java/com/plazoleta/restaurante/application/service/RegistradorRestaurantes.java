package com.plazoleta.restaurante.application.service;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.plazoleta.restaurante.domain.api.RegistrarRestaurante;
import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.domain.spi.RepositorioRestaurantesPuerto;
import com.plazoleta.restaurante.domain.spi.UsuariosRemotosPuerto;

/**
 * Implementación HU2: alta de restaurantes por parte del administrador.
 *
 * Revisa los formatos exigidos por el enunciado (NIT numérico, teléfono
 * numérico de hasta 13 caracteres con '+' opcional, nombre no compuesto
 * solo por números) y, antes de guardar, confirma con el microservicio de
 * usuarios que el dueño indicado tenga el rol PROPIETARIO.
 */
@Service
public class RegistradorRestaurantes implements RegistrarRestaurante {

    private static final Pattern SOLO_DIGITOS = Pattern.compile("^\\d+$");
    private static final Pattern TELEFONO_PERMITIDO = Pattern.compile("^\\+?\\d+$");
    private static final int LONGITUD_MAXIMA_TELEFONO = 13;

    private final RepositorioRestaurantesPuerto repositorioRestaurantes;
    private final UsuariosRemotosPuerto usuariosRemotos;

    public RegistradorRestaurantes(RepositorioRestaurantesPuerto repositorioRestaurantes,
                                    UsuariosRemotosPuerto usuariosRemotos) {
        this.repositorioRestaurantes = repositorioRestaurantes;
        this.usuariosRemotos = usuariosRemotos;
    }

    @Override
    public Restaurante ejecutar(Restaurante candidato) {
        revisarDatos(candidato);
        usuariosRemotos.confirmarPropietario(candidato.idPropietario());
        return repositorioRestaurantes.guardar(candidato);
    }

    private void revisarDatos(Restaurante candidato) {
        if (candidato == null) {
            throw new ReglaNegocioException("El cuerpo de la petición es obligatorio");
        }
        exigirPresente(candidato.nombre(), "nombre del restaurante");
        if (SOLO_DIGITOS.matcher(candidato.nombre()).matches()) {
            throw new ReglaNegocioException(
                    "El nombre del restaurante puede llevar números, pero no componerse solo de números");
        }
        exigirPresente(candidato.nit(), "NIT del restaurante");
        if (!SOLO_DIGITOS.matcher(candidato.nit()).matches()) {
            throw new ReglaNegocioException("El NIT del restaurante solo admite dígitos");
        }
        exigirPresente(candidato.direccion(), "dirección del restaurante");
        exigirPresente(candidato.urlLogo(), "dirección web del logo");
        String telefono = candidato.telefono();
        if (telefono == null
                || !TELEFONO_PERMITIDO.matcher(telefono).matches()
                || telefono.length() > LONGITUD_MAXIMA_TELEFONO) {
            throw new ReglaNegocioException(
                    "El teléfono solo admite dígitos, un '+' inicial y máximo 13 caracteres");
        }
        if (candidato.idPropietario() == null || candidato.idPropietario() <= 0) {
            throw new ReglaNegocioException(
                    "Debe indicarse el propietario que administrará el restaurante");
        }
    }

    private void exigirPresente(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("El campo " + campo + " no puede quedar vacío");
        }
    }
}
