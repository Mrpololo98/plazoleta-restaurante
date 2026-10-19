package com.plazoleta.restaurante.infrastructure.integraciones.remoto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Vista de una cuenta del microservicio de usuarios tal como la devuelve su
 * endpoint de consulta por identificador; el rol es lo relevante aquí.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CuentaUsuarioRemota(
        Long id,
        String rol) {
}
