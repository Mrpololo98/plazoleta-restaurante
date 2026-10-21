package com.plazoleta.restaurante.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.domain.spi.RepositorioEmpleadosPuerto;

/**
 * Adaptador de salida: implementa el puerto del vínculo empleado-restaurante
 * sobre JPA. El mapeo es tan corto que se hace a mano, sin conversor.
 */
@Component
public class PersistenciaEmpleadosAdapter implements RepositorioEmpleadosPuerto {

    private final RepositorioEmpleadosJpa repositorioJpa;

    public PersistenciaEmpleadosAdapter(RepositorioEmpleadosJpa repositorioJpa) {
        this.repositorioJpa = repositorioJpa;
    }

    @Override
    public Empleado guardar(Empleado empleado) {
        EmpleadoEntidad entidad = repositorioJpa.save(new EmpleadoEntidad(
                empleado.id(), empleado.idUsuario(), empleado.idRestaurante()));
        return new Empleado(entidad.getId(), entidad.getIdUsuario(), entidad.getIdRestaurante());
    }

    @Override
    public Optional<Empleado> obtenerPorIdUsuario(Long idUsuario) {
        return repositorioJpa.findByIdUsuario(idUsuario)
                .map(entidad -> new Empleado(
                        entidad.getId(), entidad.getIdUsuario(), entidad.getIdRestaurante()));
    }
}
