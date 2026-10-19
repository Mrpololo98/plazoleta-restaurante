package com.plazoleta.restaurante.infrastructure.configuration;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.plazoleta.restaurante.domain.model.TipoDePlato;
import com.plazoleta.restaurante.infrastructure.persistence.PlatoEntidad;
import com.plazoleta.restaurante.infrastructure.persistence.RepositorioPlatosJpa;
import com.plazoleta.restaurante.infrastructure.persistence.RepositorioRestaurantesJpa;
import com.plazoleta.restaurante.infrastructure.persistence.RestauranteEntidad;

/**
 * Semilla de datos demo: la primera vez que el microservicio arranca con las
 * tablas de restaurantes y platos vacías crea dos establecimientos de
 * muestra y ocho platos de carta (seis habilitados y dos inhabilitados para
 * poder ejercitar la HU7).
 *
 * Los dueños corresponden a las cuentas propietario que precarga el
 * microservicio de usuarios con su base vacía (Camila Ríos y Jorge Barajas),
 * por eso conviene arrancarlo primero.
 *
 * El inicializador jamás toca tablas que ya contengan datos.
 */
@Component
public class PrecargadorDatosDemo implements CommandLineRunner {

    private static final Logger BITACORA = LoggerFactory.getLogger(PrecargadorDatosDemo.class);

    private final RepositorioRestaurantesJpa repositorioRestaurantes;
    private final RepositorioPlatosJpa repositorioPlatos;

    @Value("${semilla.id-propietario-brasa:2}")
    private Long idPropietarioBrasa;

    @Value("${semilla.id-propietario-wok:3}")
    private Long idPropietarioWok;

    public PrecargadorDatosDemo(RepositorioRestaurantesJpa repositorioRestaurantes,
                                RepositorioPlatosJpa repositorioPlatos) {
        this.repositorioRestaurantes = repositorioRestaurantes;
        this.repositorioPlatos = repositorioPlatos;
    }

    @Override
    @Transactional
    public void run(String... argumentos) {
        if (repositorioRestaurantes.count() > 0 || repositorioPlatos.count() > 0) {
            BITACORA.info("La base ya tiene restaurantes o platos: no se cargan datos demo");
            return;
        }

        RestauranteEntidad brasaDorada = repositorioRestaurantes.save(new RestauranteEntidad(
                null, "La Brasa Dorada", "900112233", "Avenida Siempre Viva 742",
                "+573002221001", "https://img.demo-plazoleta.co/la-brasa-dorada.png",
                idPropietarioBrasa));
        RestauranteEntidad wokExpress = repositorioRestaurantes.save(new RestauranteEntidad(
                null, "Wok Express", "900445566", "Carrera 45 # 23-11",
                "+573002221002", "https://img.demo-plazoleta.co/wok-express.png",
                idPropietarioWok));

        repositorioPlatos.saveAll(List.of(
                plato("Parrillada de la Casa", TipoDePlato.PRINCIPAL,
                        "Parrillada mixta con chorizo, chicharrón y arepa.", 39500, brasaDorada, true),
                plato("Arepa Rellena", TipoDePlato.ENTRADA,
                        "Arepa asada rellena de queso campesino.", 9500, brasaDorada, true),
                plato("Limonada de Coco", TipoDePlato.BEBIDA,
                        "Limonada fresca batida con pulpa de coco.", 7500, brasaDorada, true),
                plato("Churrasco a la Brasa", TipoDePlato.PRINCIPAL,
                        "Corte de res madurado al carbón con chimichurri.", 34500, brasaDorada, true),
                plato("Wok de Pollo Teriyaki", TipoDePlato.PRINCIPAL,
                        "Salteado oriental de pollo con salsa teriyaki y sésamo.", 28900, wokExpress, true),
                plato("Rollitos Primavera", TipoDePlato.ENTRADA,
                        "Rollitos crujientes de verduras con salsa agridulce.", 11900, wokExpress, true),
                plato("Helado de Té Verde", TipoDePlato.POSTRE,
                        "Helado cremoso de té verde japonés.", 8900, wokExpress, false),
                plato("Gaseosa Personal", TipoDePlato.BEBIDA,
                        "Bebida gaseosa de 400 ml.", 4500, wokExpress, false)));

        BITACORA.info("Datos demo listos: dos restaurantes y ocho platos de carta");
    }

    private PlatoEntidad plato(String nombre, TipoDePlato categoria, String descripcion,
                               int precio, RestauranteEntidad restaurante, boolean activo) {
        return new PlatoEntidad(null, nombre, categoria, descripcion,
                BigDecimal.valueOf(precio),
                "https://img.demo-plazoleta.co/platos/"
                        + nombre.toLowerCase().replace(' ', '-') + ".png",
                restaurante.getId(), activo);
    }
}
