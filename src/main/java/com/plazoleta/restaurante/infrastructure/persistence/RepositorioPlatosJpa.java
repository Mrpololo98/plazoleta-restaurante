package com.plazoleta.restaurante.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.plazoleta.restaurante.domain.model.TipoDePlato;

/**
 * Repositorio JPA de platos.
 */
public interface RepositorioPlatosJpa extends JpaRepository<PlatoEntidad, Long> {

    /** Carta completa del restaurante: solo platos habilitados (HU10). */
    Page<PlatoEntidad> findByIdRestauranteAndActivoTrue(Long idRestaurante, Pageable pageable);

    /** Carta filtrada por categoría (HU10). */
    Page<PlatoEntidad> findByIdRestauranteAndActivoTrueAndCategoria(
            Long idRestaurante, TipoDePlato categoria, Pageable pageable);
}
