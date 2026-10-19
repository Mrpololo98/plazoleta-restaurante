package com.plazoleta.restaurante.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Personaliza la información general de la documentación OpenAPI.
 */
@Configuration
public class ConfiguracionOpenApi {

    @Bean
    public OpenAPI documentacionPlazoleta() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Restaurantes · Plazoleta de Comidas")
                        .version("1.0.0")
                        .description("Servicio de restaurantes, cartas de platos y contratación "
                                + "de empleados de la plazoleta de comidas. Valida los tokens "
                                + "que emite el microservicio de usuarios. Proyecto educativo "
                                + "con arquitectura hexagonal."));
    }
}
