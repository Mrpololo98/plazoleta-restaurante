package com.plazoleta.restaurante.infrastructure.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plazoleta.restaurante.application.dto.PeticionVincularEmpleado;
import com.plazoleta.restaurante.application.dto.RespuestaEmpleado;
import com.plazoleta.restaurante.domain.api.VincularEmpleado;
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.infrastructure.security.PortadorAutenticado;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Endpoint de contratación (HU6): crea la cuenta del empleado en el
 * microservicio de usuarios (reenviando el token del propietario por Feign)
 * y guarda aquí el vínculo laboral con el restaurante.
 */
@RestController
@RequestMapping("/empleados")
@Tag(name = "Empleados", description = "Contratación de personal para un restaurante")
public class EmpleadoControlador {

    private final VincularEmpleado vincularEmpleado;
    private final ConversorEmpleadosRest conversor;

    public EmpleadoControlador(VincularEmpleado vincularEmpleado,
                               ConversorEmpleadosRest conversor) {
        this.vincularEmpleado = vincularEmpleado;
        this.conversor = conversor;
    }

    @Operation(summary = "Contrata un empleado para un restaurante",
            description = "El propietario del restaurante crea la cuenta de un empleado: los "
                    + "datos personales viajan al microservicio de usuarios, que los guarda "
                    + "con rol EMPLEADO, y este microservicio registra el vínculo laboral.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Empleado contratado y vinculado"),
            @ApiResponse(responseCode = "400", description = "Alguna regla de negocio no se cumplió"),
            @ApiResponse(responseCode = "401", description = "Falta token de acceso"),
            @ApiResponse(responseCode = "403", description = "El rol del token no es PROPIETARIO"),
            @ApiResponse(responseCode = "404", description = "El restaurante o la cuenta no existe")
    })
    @PostMapping
    public ResponseEntity<RespuestaEmpleado> contratar(
            @AuthenticationPrincipal PortadorAutenticado portador,
            @Valid @RequestBody PeticionVincularEmpleado peticion) {
        Empleado vinculo = vincularEmpleado.ejecutar(
                conversor.aSolicitud(peticion), peticion.idRestaurante(), portador.idUsuario());
        return ResponseEntity.status(HttpStatus.CREATED).body(conversor.aRespuesta(vinculo));
    }
}
