package com.plazoleta.restaurante.application.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.plazoleta.restaurante.domain.api.ConsultarEmpleado;
import com.plazoleta.restaurante.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.domain.spi.RepositorioEmpleadosPuerto;

/**
 * Implementación del caso de uso de integración ConsultarEmpleado: resuelve
 * a qué restaurante está vinculada una cuenta de empleado, dato con el que
 * el microservicio de pedidos filtra las órdenes que el empleado puede ver.
 */
@Service
public class ConsultorEmpleados implements ConsultarEmpleado {

    private final RepositorioEmpleadosPuerto repositorioEmpleados;

    public ConsultorEmpleados(RepositorioEmpleadosPuerto repositorioEmpleados) {
        this.repositorioEmpleados = repositorioEmpleados;
    }

    @Override
    public Empleado ejecutar(Long idUsuario) {
        Optional<Empleado> vinculo = repositorioEmpleados.obtenerPorIdUsuario(idUsuario);
        return vinculo.orElseThrow(() -> new RecursoNoEncontradoException(
                "La cuenta " + idUsuario + " no está vinculada como empleado de ningún restaurante"));
    }
}
