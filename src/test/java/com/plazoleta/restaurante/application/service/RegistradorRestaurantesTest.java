package com.plazoleta.restaurante.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.plazoleta.restaurante.domain.exception.ReglaNegocioException;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.domain.spi.RepositorioRestaurantesPuerto;
import com.plazoleta.restaurante.domain.spi.UsuariosRemotosPuerto;

/**
 * Pruebas unitarias del caso de uso HU2 (alta de restaurantes).
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegistradorRestaurantesTest {

    @Mock
    private RepositorioRestaurantesPuerto repositorioRestaurantes;

    @Mock
    private UsuariosRemotosPuerto usuariosRemotos;

    private RegistradorRestaurantes registrador;

    @BeforeEach
    void preparar() {
        registrador = new RegistradorRestaurantes(repositorioRestaurantes, usuariosRemotos);
    }

    private Restaurante candidatoValido() {
        return new Restaurante(null, "La Brasa Dorada", "900112233",
                "Avenida Siempre Viva 742", "+573002221001",
                "https://img.demo-plazoleta.co/logo.png", 2L);
    }

    @Test
    void debeRegistrarConfirmandoAntesElRolDelPropietario() {
        org.mockito.Mockito.when(repositorioRestaurantes.guardar(any(Restaurante.class)))
                .thenAnswer(invocacion -> {
                    Restaurante entrante = invocacion.getArgument(0);
                    return new Restaurante(7L, entrante.nombre(), entrante.nit(),
                            entrante.direccion(), entrante.telefono(),
                            entrante.urlLogo(), entrante.idPropietario());
                });

        Restaurante guardado = registrador.ejecutar(candidatoValido());

        assertEquals(7L, guardado.id());
        assertThat(guardado.nombre()).isEqualTo("La Brasa Dorada");
        verify(usuariosRemotos).confirmarPropietario(2L);
        verify(repositorioRestaurantes).guardar(any(Restaurante.class));
    }

    @Test
    void debeRechazarUnNombreCompuestoSoloPorNumeros() {
        Restaurante candidato = new Restaurante(null, "54321", "900112233",
                "Avenida Siempre Viva 742", "+573002221001",
                "https://img.demo-plazoleta.co/logo.png", 2L);

        assertThatThrownBy(() -> registrador.ejecutar(candidato))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("solo de números");
        verifyNoInteractions(repositorioRestaurantes, usuariosRemotos);
    }

    @Test
    void debeRechazarUnNitConLetras() {
        Restaurante candidato = new Restaurante(null, "La Brasa Dorada", "900-ABC1",
                "Avenida Siempre Viva 742", "+573002221001",
                "https://img.demo-plazoleta.co/logo.png", 2L);

        assertThatThrownBy(() -> registrador.ejecutar(candidato))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("NIT");
        verifyNoInteractions(repositorioRestaurantes, usuariosRemotos);
    }

    @Test
    void debeRechazarUnTelefonoInvalidoOMasLargoDeTrece() {
        Restaurante telefonoConLetras = new Restaurante(null, "La Brasa Dorada", "900112233",
                "Avenida Siempre Viva 742", "+57 300 ABC",
                "https://img.demo-plazoleta.co/logo.png", 2L);
        assertThatThrownBy(() -> registrador.ejecutar(telefonoConLetras))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("teléfono");

        Restaurante telefonoLargo = new Restaurante(null, "La Brasa Dorada", "900112233",
                "Avenida Siempre Viva 742", "+573002221001999",
                "https://img.demo-plazoleta.co/logo.png", 2L);
        assertThatThrownBy(() -> registrador.ejecutar(telefonoLargo))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("teléfono");

        verifyNoInteractions(repositorioRestaurantes, usuariosRemotos);
    }

    @Test
    void debeRechazarLaFaltaDePropietario() {
        Restaurante sinPropietario = new Restaurante(null, "La Brasa Dorada", "900112233",
                "Avenida Siempre Viva 742", "+573002221001",
                "https://img.demo-plazoleta.co/logo.png", null);

        assertThatThrownBy(() -> registrador.ejecutar(sinPropietario))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("propietario");
        verifyNoInteractions(repositorioRestaurantes, usuariosRemotos);
    }
}
