package com.plazoleta.restaurante.infrastructure.integraciones.remoto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Respuesta del microservicio de usuarios al crear una cuenta: de todo el
 * cuerpo devuelto solo interesa el identificador asignado.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RespuestaCuentaRemota(
        Long id) {
}
