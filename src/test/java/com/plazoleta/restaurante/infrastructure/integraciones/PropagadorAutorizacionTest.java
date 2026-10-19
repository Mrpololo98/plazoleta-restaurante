package com.plazoleta.restaurante.infrastructure.integraciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestTemplate;

/**
 * Pruebas del interceptor que reenvía el token a las llamadas Feign.
 */
class PropagadorAutorizacionTest {

    private final PropagadorAutorizacion propagador = new PropagadorAutorizacion();

    @AfterEach
    void limpiar() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void debeReenviarElEncabezadoDeAutorizacionEntrante() {
        MockHttpServletRequest servlet = new MockHttpServletRequest();
        servlet.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token-demo");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servlet));

        RequestTemplate plantilla = new RequestTemplate();
        propagador.apply(plantilla);

        assertTrue(plantilla.headers().containsKey(HttpHeaders.AUTHORIZATION));
        assertEquals("Bearer token-demo",
                plantilla.headers().get(HttpHeaders.AUTHORIZATION).iterator().next());
    }

    @Test
    void debeDejarLaLlamadaLimpiaFueraDeUnaPeticionHttp() {
        RequestTemplate plantilla = new RequestTemplate();
        propagador.apply(plantilla);

        assertFalse(plantilla.headers().containsKey(HttpHeaders.AUTHORIZATION));
    }
}
