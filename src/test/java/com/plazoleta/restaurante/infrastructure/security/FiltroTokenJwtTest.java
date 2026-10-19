package com.plazoleta.restaurante.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Pruebas del filtro que valida en este microservicio los JWT emitidos por
 * el microservicio de usuarios (mismo secreto, mismos claims).
 */
class FiltroTokenJwtTest {

    private static final String SECRETO_PRUEBAS =
            "clave-de-pruebas-para-el-filtro-de-restaurantes-1234567890";

    private FiltroTokenJwt filtro;

    @BeforeEach
    void preparar() {
        SecurityContextHolder.clearContext();
        filtro = new FiltroTokenJwt(SECRETO_PRUEBAS);
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    private String tokenValido() {
        return Jwts.builder()
                .subject("camila.rios@demo-plazoleta.co")
                .claim("idUsuario", 2L)
                .claim("rol", "PROPIETARIO")
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(Keys.hmacShaKeyFor(SECRETO_PRUEBAS.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    @Test
    void debeDejarAlPortadorEnElContextoDeSeguridad() throws Exception {
        MockHttpServletRequest peticion = new MockHttpServletRequest();
        peticion.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValido());
        MockHttpServletResponse respuesta = new MockHttpServletResponse();

        filtro.doFilter(peticion, respuesta, (solicitud, replica) -> { });

        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(autenticacion);
        PortadorAutenticado portador = (PortadorAutenticado) autenticacion.getPrincipal();
        assertEquals(2L, portador.idUsuario());
        assertEquals("camila.rios@demo-plazoleta.co", portador.correo());
        assertEquals("PROPIETARIO", portador.rol());
        assertTrue(autenticacion.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_PROPIETARIO")));
    }

    @Test
    void debeRechazarCon401UnTokenAlterado() throws Exception {
        MockHttpServletRequest peticion = new MockHttpServletRequest();
        peticion.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValido() + "ruido");
        MockHttpServletResponse respuesta = new MockHttpServletResponse();

        filtro.doFilter(peticion, respuesta, (solicitud, replica) -> { });

        assertEquals(401, respuesta.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
