package com.plazoleta.restaurante.infrastructure.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Responde 401 en formato JSON cuando una petición llega sin autenticación
 * a un endpoint protegido.
 */
@Component
public class PuntoEntradaNoAutorizado implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest peticion,
                         HttpServletResponse respuesta,
                         AuthenticationException excepcion) throws IOException {
        respuesta.setStatus(HttpStatus.UNAUTHORIZED.value());
        respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        respuesta.setCharacterEncoding(java.nio.charset.StandardCharsets.UTF_8.name());
        respuesta.getWriter().write(
                "{\"estado\":" + HttpStatus.UNAUTHORIZED.value()
                        + ",\"mensaje\":\"Se requiere iniciar sesión para usar este recurso\""
                        + ",\"ocurridoEn\":\"" + Instant.now() + "\"}");
    }
}
