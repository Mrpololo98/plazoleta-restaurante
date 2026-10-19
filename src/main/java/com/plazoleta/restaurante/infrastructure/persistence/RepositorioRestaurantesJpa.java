package com.plazoleta.restaurante.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA de restaurantes.
 */
public interface RepositorioRestaurantesJpa extends JpaRepository<RestauranteEntidad, Long> {
}
