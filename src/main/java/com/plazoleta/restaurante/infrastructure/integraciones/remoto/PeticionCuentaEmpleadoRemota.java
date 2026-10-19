package com.plazoleta.restaurante.infrastructure.integraciones.remoto;

/**
 * Cuerpo que viaja al microservicio de usuarios para pedir la creación de la
 * cuenta del empleado (HU6). La clave va en texto plano solo en este salto:
 * es el microservicio de usuarios quien la protege con BCrypt al guardarla.
 */
public record PeticionCuentaEmpleadoRemota(
        String nombre,
        String apellido,
        String documentoIdentidad,
        String celular,
        String correo,
        String clave) {
}
