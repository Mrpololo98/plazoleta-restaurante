package com.plazoleta.restaurante.infrastructure.integraciones;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Reenvía el encabezado Authorization de la petición HTTP entrante a cada
 * llamada Feign hacia el microservicio de usuarios. Sin esto, los endpoints
 * protegidos del otro micro rechazarían la operación por falta de token.
 */
@Component
public class PropagadorAutorizacion implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate plantilla) {
        if (RequestContextHolder.getRequestAttributes()
                instanceof ServletRequestAttributes atributos) {
            String autorizacion = atributos.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (autorizacion != null && !autorizacion.isBlank()) {
                plantilla.header(HttpHeaders.AUTHORIZATION, autorizacion);
            }
        }
    }
}
