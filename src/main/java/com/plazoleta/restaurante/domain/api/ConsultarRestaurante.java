package com.plazoleta.restaurante.domain.api;

import com.plazoleta.restaurante.domain.model.Restaurante;

/**
 * Caso de uso de integración: consulta un restaurante por su identificador.
 *
 * Lo usa el microservicio de pedidos para confirmar que quien pide las
 * métricas de eficiencia (HU18) es el dueño del establecimiento.
 */
public interface ConsultarRestaurante {

    Restaurante ejecutar(Long id);
}
