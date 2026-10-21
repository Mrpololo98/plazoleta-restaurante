package com.plazoleta.restaurante.domain.api;

import com.plazoleta.restaurante.domain.model.Plato;

/**
 * Caso de uso de integración: consulta un plato por su identificador.
 *
 * Lo usa el microservicio de pedidos para confirmar que los platos de una
 * orden existen, siguen habilitados y pertenecen al restaurante indicado.
 */
public interface ConsultarPlato {

    Plato ejecutar(Long id);
}
