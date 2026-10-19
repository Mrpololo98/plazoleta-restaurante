package com.plazoleta.restaurante.infrastructure.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.plazoleta.restaurante.application.dto.PeticionRegistrarRestaurante;
import com.plazoleta.restaurante.application.dto.RespuestaHojaRestaurantes;
import com.plazoleta.restaurante.application.dto.RespuestaRestaurante;
import com.plazoleta.restaurante.domain.api.ListarRestaurantes;
import com.plazoleta.restaurante.domain.api.RegistrarRestaurante;
import com.plazoleta.restaurante.domain.model.Restaurante;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Endpoints de restaurantes: alta a cargo del administrador (HU2) y
 * listado público, alfabético y paginado, para explorar la plazoleta (HU9).
 */
@RestController
@RequestMapping("/restaurantes")
@Tag(name = "Restaurantes", description = "Alta de restaurantes y exploración de la plazoleta")
public class RestauranteControlador {

    private final RegistrarRestaurante registrarRestaurante;
    private final ListarRestaurantes listarRestaurantes;
    private final ConversorRestaurantesRest conversor;

    public RestauranteControlador(RegistrarRestaurante registrarRestaurante,
                                  ListarRestaurantes listarRestaurantes,
                                  ConversorRestaurantesRest conversor) {
        this.registrarRestaurante = registrarRestaurante;
        this.listarRestaurantes = listarRestaurantes;
        this.conversor = conversor;
    }

    @Operation(summary = "Registra un restaurante nuevo",
            description = "Permite al administrador dar de alta un establecimiento. Antes de "
                    + "guardar se consulta el microservicio de usuarios para confirmar que el "
                    + "dueño indicado tiene el rol PROPIETARIO.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurante creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Alguna regla de negocio no se cumplió"),
            @ApiResponse(responseCode = "401", description = "Falta token de acceso"),
            @ApiResponse(responseCode = "403", description = "El rol del token no es ADMIN")
    })
    @PostMapping
    public ResponseEntity<RespuestaRestaurante> crear(
            @Valid @RequestBody PeticionRegistrarRestaurante peticion) {
        Restaurante guardado = registrarRestaurante.ejecutar(conversor.aDominio(peticion));
        return ResponseEntity.status(HttpStatus.CREATED).body(conversor.aRespuesta(guardado));
    }

    @Operation(summary = "Lista los restaurantes de la plazoleta",
            description = "Devuelve los restaurantes en orden alfabético y por hojas; de cada "
                    + "uno solo se publican el nombre y la dirección web de su logo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hoja de restaurantes"),
            @ApiResponse(responseCode = "400", description = "Paginación solicitada no válida")
    })
    @GetMapping
    public ResponseEntity<RespuestaHojaRestaurantes> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int cantidad) {
        return ResponseEntity.ok(conversor.aRespuestaHoja(listarRestaurantes.ejecutar(pagina, cantidad)));
    }
}
