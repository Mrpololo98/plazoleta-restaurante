package com.plazoleta.restaurante;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Microservicio de restaurantes de la plazoleta de comidas.
 *
 * Cubre las historias de usuario HU2 (restaurantes), HU3 y HU4 (platos),
 * HU7 (disponibilidad de platos), HU6 (contratación de empleados) y
 * HU9 (listado de restaurantes), además de la parte de la HU5 que valida
 * los tokens emitidos por el microservicio de usuarios.
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.plazoleta.restaurante.infrastructure.integraciones")
public class PlazoletaRestaurantesApplication {

    public static void main(String[] argumentos) {
        SpringApplication.run(PlazoletaRestaurantesApplication.class, argumentos);
    }
}
