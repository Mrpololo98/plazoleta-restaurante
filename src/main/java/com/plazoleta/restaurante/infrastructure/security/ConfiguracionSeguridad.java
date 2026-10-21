package com.plazoleta.restaurante.infrastructure.security;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Seguridad HTTP del microservicio (parte restaurante de la HU5).
 *
 * - GET   /restaurantes y /restaurantes/** → público (HU9 y HU10: explorar
 *   la plazoleta y leer la carta de cada restaurante; la ficha de un
 *   establecimiento no es reservada)
 * - GET   /platos/{id}    → público (integración: pedidos valida sus órdenes)
 * - POST  /restaurantes   → solo ADMIN (HU2)
 * - POST  /platos         → solo PROPIETARIO (HU3)
 * - PUT   /platos/**      → solo PROPIETARIO (HU4)
 * - PATCH /platos/**      → solo PROPIETARIO (HU7)
 * - POST  /empleados      → solo PROPIETARIO (HU6)
 * - GET   /empleados/{id} → autenticado (integración: pedidos resuelve el
 *   restaurante de un empleado)
 * - Swagger y /error      → público
 *
 * La API no guarda sesión: cada petición viaja con su JWT.
 */
@Configuration
@EnableWebSecurity
public class ConfiguracionSeguridad {

    @Bean
    public SecurityFilterChain cadenaSeguridad(HttpSecurity http,
                                               PuntoEntradaNoAutorizado puntoEntrada,
                                               @Value("${seguridad.jwt.secreto}") String secreto)
            throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(errores -> errores
                    .authenticationEntryPoint(puntoEntrada)
                    .accessDeniedHandler((peticion, respuesta, denegado) -> {
                        respuesta.setStatus(HttpStatus.FORBIDDEN.value());
                        respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        respuesta.setCharacterEncoding(java.nio.charset.StandardCharsets.UTF_8.name());
                        respuesta.getWriter().write(
                                "{\"estado\":403,\"mensaje\":\"Su rol no tiene permiso para esta operación\""
                                        + ",\"ocurridoEn\":\"" + Instant.now() + "\"}");
                    }))
            .authorizeHttpRequests(reglas -> reglas
                    .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/error")
                        .permitAll()
                    .requestMatchers(HttpMethod.GET, "/restaurantes").permitAll()
                    .requestMatchers(HttpMethod.GET, "/restaurantes/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/platos/*").permitAll()
                    .requestMatchers(HttpMethod.POST, "/restaurantes").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/platos").hasRole("PROPIETARIO")
                    .requestMatchers(HttpMethod.PUT, "/platos/**").hasRole("PROPIETARIO")
                    .requestMatchers(HttpMethod.PATCH, "/platos/**").hasRole("PROPIETARIO")
                    .requestMatchers(HttpMethod.POST, "/empleados").hasRole("PROPIETARIO")
                    .anyRequest().authenticated())
            .addFilterBefore(new FiltroTokenJwt(secreto), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
