package com.plazoleta.restaurante.infrastructure.rest;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plazoleta.restaurante.application.dto.PeticionAjustarPlato;
import com.plazoleta.restaurante.application.dto.PeticionCambiarDisponibilidadPlato;
import com.plazoleta.restaurante.application.dto.PeticionRegistrarPlato;
import com.plazoleta.restaurante.application.dto.RespuestaPlato;
import com.plazoleta.restaurante.domain.api.AjustarPlato;
import com.plazoleta.restaurante.domain.api.AlternarPlato;
import com.plazoleta.restaurante.domain.api.RegistrarPlato;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.infrastructure.security.PortadorAutenticado;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Endpoints de la carta: alta de platos (HU3), corrección de precio y
 * descripción (HU4) y habilitación o inhabilitación (HU7). El propietario
 * responsable sale del token, nunca de la petición.
 */
@RestController
@RequestMapping("/platos")
@Tag(name = "Platos", description = "Carta de los restaurantes: alta, ajuste y disponibilidad")
public class PlatoControlador {

    private final RegistrarPlato registrarPlato;
    private final AjustarPlato ajustarPlato;
    private final AlternarPlato alternarPlato;
    private final ConversorPlatosRest conversor;

    public PlatoControlador(RegistrarPlato registrarPlato,
                            AjustarPlato ajustarPlato,
                            AlternarPlato alternarPlato,
                            ConversorPlatosRest conversor) {
        this.registrarPlato = registrarPlato;
        this.ajustarPlato = ajustarPlato;
        this.alternarPlato = alternarPlato;
        this.conversor = conversor;
    }

    @Operation(summary = "Agrega un plato a la carta",
            description = "Solo el propietario dueño del restaurante puede agregar platos. "
                    + "El plato nace habilitado y su precio debe ser un entero positivo.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plato agregado a la carta"),
            @ApiResponse(responseCode = "400", description = "Alguna regla de negocio no se cumplió"),
            @ApiResponse(responseCode = "401", description = "Falta token de acceso"),
            @ApiResponse(responseCode = "403", description = "El rol del token no es PROPIETARIO"),
            @ApiResponse(responseCode = "404", description = "El restaurante no existe")
    })
    @PostMapping
    public ResponseEntity<RespuestaPlato> crear(
            @AuthenticationPrincipal PortadorAutenticado portador,
            @Valid @RequestBody PeticionRegistrarPlato peticion) {
        Plato guardado = registrarPlato.ejecutar(conversor.aDominio(peticion), portador.idUsuario());
        return ResponseEntity.status(HttpStatus.CREATED).body(conversor.aRespuesta(guardado));
    }

    @Operation(summary = "Corrige el precio y la descripción de un plato",
            description = "Actualiza únicamente esos dos campos: la categoría, la imagen y el "
                    + "restaurante de origen no se tocan. Exige ser dueño del restaurante del plato.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plato corregido"),
            @ApiResponse(responseCode = "400", description = "Alguna regla de negocio no se cumplió"),
            @ApiResponse(responseCode = "401", description = "Falta token de acceso"),
            @ApiResponse(responseCode = "403", description = "El rol del token no es PROPIETARIO"),
            @ApiResponse(responseCode = "404", description = "El plato o el restaurante no existe")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RespuestaPlato> ajustar(
            @AuthenticationPrincipal PortadorAutenticado portador,
            @PathVariable Long id,
            @Valid @RequestBody PeticionAjustarPlato peticion) {
        Plato ajustado = ajustarPlato.ejecutar(id, peticion.precio(), peticion.descripcion(),
                portador.idUsuario());
        return ResponseEntity.ok(conversor.aRespuesta(ajustado));
    }

    @Operation(summary = "Habilita o inhabilita un plato",
            description = "Cambia la disponibilidad del plato en la carta sin alterar ningún otro "
                    + "dato. Solo el dueño del restaurante puede hacerlo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada"),
            @ApiResponse(responseCode = "400", description = "Alguna regla de negocio no se cumplió"),
            @ApiResponse(responseCode = "401", description = "Falta token de acceso"),
            @ApiResponse(responseCode = "403", description = "El rol del token no es PROPIETARIO"),
            @ApiResponse(responseCode = "404", description = "El plato o el restaurante no existe")
    })
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<RespuestaPlato> cambiarDisponibilidad(
            @AuthenticationPrincipal PortadorAutenticado portador,
            @PathVariable Long id,
            @Valid @RequestBody PeticionCambiarDisponibilidadPlato peticion) {
        Plato cambiado = alternarPlato.ejecutar(id, peticion.activo(), portador.idUsuario());
        return ResponseEntity.ok(conversor.aRespuesta(cambiado));
    }
}
