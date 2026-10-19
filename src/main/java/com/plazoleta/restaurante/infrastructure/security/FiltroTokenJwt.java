package com.plazoleta.restaurante.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro de la parte restaurante de la HU5: este microservicio no emite
 * tokens, solo VALIDA con el secreto compartido los JWT que emite el
 * microservicio de usuarios. Si el token supera la verificación, su
 * portador queda en el contexto de seguridad con su rol como autoridad.
 * Un token ausente deja pasar la petición sin autenticación: el acceso por
 * endpoint lo decide la configuración de seguridad; uno inválido responde 401.
 */
public class FiltroTokenJwt extends OncePerRequestFilter {

    private final SecretKey claveDeFirma;

    public FiltroTokenJwt(String secreto) {
        this.claveDeFirma = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest peticion,
                                    HttpServletResponse respuesta,
                                    FilterChain cadena) throws ServletException, IOException {
        String encabezado = peticion.getHeader(HttpHeaders.AUTHORIZATION);

        if (encabezado != null && encabezado.startsWith("Bearer ")) {
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(claveDeFirma)
                        .build()
                        .parseSignedClaims(encabezado.substring(7))
                        .getPayload();

                PortadorAutenticado portador = new PortadorAutenticado(
                        claims.get("idUsuario", Long.class),
                        claims.getSubject(),
                        claims.get("rol", String.class));

                UsernamePasswordAuthenticationToken autenticacion =
                        new UsernamePasswordAuthenticationToken(
                                portador, null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + portador.rol())));
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            } catch (JwtException | IllegalArgumentException excepcion) {
                SecurityContextHolder.clearContext();
                responderNoAutorizado(respuesta,
                        "El token de acceso no es válido o ya expiró");
                return;
            }
        }

        cadena.doFilter(peticion, respuesta);
    }

    private void responderNoAutorizado(HttpServletResponse respuesta, String mensaje) throws IOException {
        respuesta.setStatus(HttpStatus.UNAUTHORIZED.value());
        respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        respuesta.setCharacterEncoding(StandardCharsets.UTF_8.name());
        respuesta.getWriter().write(
                "{\"estado\":" + HttpStatus.UNAUTHORIZED.value()
                        + ",\"mensaje\":\"" + mensaje + "\""
                        + ",\"ocurridoEn\":\"" + Instant.now() + "\"}");
    }
}
