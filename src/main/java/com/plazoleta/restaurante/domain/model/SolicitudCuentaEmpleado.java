package com.plazoleta.restaurante.domain.model;

/**
 * Datos personales de la cuenta de un empleado. Este microservicio no
 * guarda credenciales: reenvía esta solicitud por Feign al microservicio
 * de usuarios, que es quien crea la cuenta con rol EMPLEADO y protege la
 * clave con BCrypt (HU6).
 */
public record SolicitudCuentaEmpleado(
        String nombre,
        String apellido,
        String documentoIdentidad,
        String celular,
        String correo,
        String clave) {
}
