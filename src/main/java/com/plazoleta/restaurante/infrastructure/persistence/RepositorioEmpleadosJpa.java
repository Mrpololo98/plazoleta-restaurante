package com.plazoleta.restaurante.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA del vínculo empleado-restaurante.
 */
public interface RepositorioEmpleadosJpa extends JpaRepository<EmpleadoEntidad, Long> {

    Optional<EmpleadoEntidad> findByIdUsuario(Long idUsuario);
}
