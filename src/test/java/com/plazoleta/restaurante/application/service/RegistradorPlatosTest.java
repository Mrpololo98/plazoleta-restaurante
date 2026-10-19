package com.plazoleta.restaurante.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.domain.model.TipoDePlato;
import com.plazoleta.restaurante.domain.spi.RepositorioPlatosPuerto;
import com.plazoleta.restaurante.domain.spi.RepositorioRestaurantesPuerto;
import com.plazoleta.restaurante.domain.spi.UsuariosRemotosPuerto;

/**
 * Pruebas unitarias del caso de uso HU3 (alta de platos).
 */
@ExtendWith(MockitoExtension.class)
class RegistradorPlatosTest {

    @Mock
    private RepositorioPlatosPuerto repositorioPlatos;

    @Mock
    private RepositorioRestaurantesPuerto repositorioRestaurantes;

    @Mock
    private UsuariosRemotosPuerto usuariosRemotos;

    private RegistradorPlatos registrador;

    @BeforeEach
    void preparar() {
        VerificadorPropiedadRestaurante verificador =
                new VerificadorPropiedadRestaurante(repositorioRestaurantes, usuariosRemotos);
        registrador = new RegistradorPlatos(repositorioPlatos, verificador);
    }

    private Plato platoCon(BigDecimal precio, Long idRestaurante) {
        return new Plato(null, "Parrillada de la Casa", TipoDePlato.PRINCIPAL,
                "Parrillada mixta de la casa", precio,
                "https://img.demo-plazoleta.co/parrillada.png", idRestaurante, null);
    }

    private Restaurante restauranteDe(Long idPropietario) {
        return new Restaurante(1L, "La Brasa Dorada", "900112233",
                "Avenida Siempre Viva 742", "+573002221001",
                "https://img.demo-plazoleta.co/logo.png", idPropietario);
    }

    @Test
    void debeCrearElPlatoHabilitadoCuandoElDuenioLoPide() {
        when(repositorioRestaurantes.obtenerPorId(1L))
                .thenReturn(Optional.of(restauranteDe(2L)));
        when(repositorioPlatos.guardar(any(Plato.class))).thenAnswer(invocacion -> {
            Plato entrante = invocacion.getArgument(0);
            return new Plato(11L, entrante.nombre(), entrante.categoria(),
                    entrante.descripcion(), entrante.precio(), entrante.urlImagen(),
                    entrante.idRestaurante(), entrante.activo());
        });

        Plato guardado = registrador.ejecutar(platoCon(BigDecimal.valueOf(39500), 1L), 2L);

        // HU3: el plato nace habilitado en la carta
        assertTrue(guardado.activo());
        assertEquals(11L, guardado.id());
        verify(usuariosRemotos).confirmarPropietario(2L);
    }

    @Test
    void debeFallarCuandoElRestauranteNoExiste() {
        when(repositorioRestaurantes.obtenerPorId(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrador.ejecutar(platoCon(BigDecimal.TEN, 9L), 2L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("9");
        verifyNoInteractions(repositorioPlatos);
    }

    @Test
    void debeFallarCuandoElRestauranteEsDeOtroPropietario() {
        when(repositorioRestaurantes.obtenerPorId(1L))
                .thenReturn(Optional.of(restauranteDe(3L)));

        assertThatThrownBy(() -> registrador.ejecutar(platoCon(BigDecimal.TEN, 1L), 2L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no pertenece");
        verifyNoInteractions(repositorioPlatos, usuariosRemotos);
    }

    @Test
    void debeRechazarPreciosConDecimalesOCero() {
        assertThatThrownBy(() -> registrador.ejecutar(platoCon(new BigDecimal("99.99"), 1L), 2L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("número entero");

        assertThatThrownBy(() -> registrador.ejecutar(platoCon(BigDecimal.ZERO, 1L), 2L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("número entero");

        verifyNoInteractions(repositorioRestaurantes, usuariosRemotos, repositorioPlatos);
    }

    @Test
    void debeRechazarLaCategoriaAusente() {
        Plato sinCategoria = new Plato(null, "Parrillada de la Casa", null,
                "Parrillada mixta de la casa", BigDecimal.valueOf(39500),
                "https://img.demo-plazoleta.co/parrillada.png", 1L, null);

        assertThatThrownBy(() -> registrador.ejecutar(sinCategoria, 2L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("categoría");
        verifyNoInteractions(repositorioRestaurantes, usuariosRemotos, repositorioPlatos);
    }
}
