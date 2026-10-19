package com.plazoleta.restaurante.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
 * Pruebas unitarias del caso de uso HU7 (habilitar o inhabilitar platos).
 */
@ExtendWith(MockitoExtension.class)
class AlternadorPlatosTest {

    @Mock
    private RepositorioPlatosPuerto repositorioPlatos;

    @Mock
    private RepositorioRestaurantesPuerto repositorioRestaurantes;

    @Mock
    private UsuariosRemotosPuerto usuariosRemotos;

    private AlternadorPlatos alternador;

    @BeforeEach
    void preparar() {
        VerificadorPropiedadRestaurante verificador =
                new VerificadorPropiedadRestaurante(repositorioRestaurantes, usuariosRemotos);
        alternador = new AlternadorPlatos(repositorioPlatos, verificador);
    }

    private Plato platoHabilitado() {
        return new Plato(5L, "Helado de Té Verde", TipoDePlato.POSTRE,
                "Helado cremoso de té verde", BigDecimal.valueOf(8900),
                "https://img.demo-plazoleta.co/helado.png", 2L, true);
    }

    private Restaurante restauranteDe(Long idPropietario) {
        return new Restaurante(2L, "Wok Express", "900445566",
                "Carrera 45 # 23-11", "+573002221002",
                "https://img.demo-plazoleta.co/wok.png", idPropietario);
    }

    @Test
    void debeInhabilitarUnPlatoSinTocarNingunOtroDato() {
        when(repositorioPlatos.obtenerPorId(5L)).thenReturn(Optional.of(platoHabilitado()));
        when(repositorioRestaurantes.obtenerPorId(2L))
                .thenReturn(Optional.of(restauranteDe(3L)));
        when(repositorioPlatos.guardar(any(Plato.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Plato cambiado = alternador.ejecutar(5L, false, 3L);

        // HU7: solo cambia la disponibilidad
        assertFalse(cambiado.activo());
        assertEquals("Helado de Té Verde", cambiado.nombre());
        assertEquals(TipoDePlato.POSTRE, cambiado.categoria());
        assertEquals(0, BigDecimal.valueOf(8900).compareTo(cambiado.precio()));
        verify(usuariosRemotos).confirmarPropietario(3L);
    }

    @Test
    void debeFallarCuandoElPlatoNoExiste() {
        when(repositorioPlatos.obtenerPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alternador.ejecutar(404L, true, 3L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("404");
        verifyNoInteractions(repositorioRestaurantes, usuariosRemotos);
    }

    @Test
    void debeRechazarLaDisponibilidadAusente() {
        assertThatThrownBy(() -> alternador.ejecutar(5L, null, 3L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("disponibilidad");
        verifyNoInteractions(repositorioPlatos, repositorioRestaurantes, usuariosRemotos);
    }
}
