package com.plazoleta.restaurante.infrastructure.integraciones;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.plazoleta.restaurante.infrastructure.integraciones.remoto.CuentaUsuarioRemota;
import com.plazoleta.restaurante.infrastructure.integraciones.remoto.PeticionCuentaEmpleadoRemota;
import com.plazoleta.restaurante.infrastructure.integraciones.remoto.RespuestaCuentaRemota;

/**
 * Cliente HTTP del microservicio de usuarios. Las rutas corresponden al
 * contrato público documentado en su Swagger:
 *
 *  - GET /usuarios/{id} → vista de la cuenta (roles para validar propietarios)
 *  - POST /empleados    → alta de cuentas de empleado (HU6)
 *
 * El token de la petición entrante viaja en cada llamada gracias al
 * {@link PropagadorAutorizacion}.
 */
@FeignClient(name = "servicio-usuarios-plazoleta", url = "${integraciones.usuario.base-url}")
public interface ClienteUsuariosApi {

    @GetMapping("/usuarios/{id}")
    CuentaUsuarioRemota consultarCuenta(@PathVariable("id") Long id);

    @PostMapping("/empleados")
    RespuestaCuentaRemota crearCuentaEmpleado(@RequestBody PeticionCuentaEmpleadoRemota peticion);
}
