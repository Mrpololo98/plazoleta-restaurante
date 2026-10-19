package com.plazoleta.restaurante.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA de platos.
 */
public interface RepositorioPlatosJpa extends JpaRepository<PlatoEntidad, Long> {
}
