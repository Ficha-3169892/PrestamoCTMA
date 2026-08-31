package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.model.EstadoSolicitud
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryPrestamoRepositoryTest {
    @Test
    fun crear_solicitud_reserva_equipo_disponible() {
        val repository = InMemoryPrestamoRepository()

        val resultado = repository.crearSolicitud(
            equipoId = 1,
            ambienteDestino = "Ambiente TIC",
            proposito = "Practica de laboratorio",
            duracionHoras = 2,
        )

        assertTrue(resultado.isSuccess)
        assertEquals(EstadoSolicitud.SOLICITADA, resultado.getOrThrow().estado)
        assertEquals(EstadoEquipo.RESERVADO, repository.obtenerEquipo(1)?.estado)
    }

    @Test
    fun no_permite_solicitar_equipo_no_disponible() {
        val repository = InMemoryPrestamoRepository()

        val resultado = repository.crearSolicitud(
            equipoId = 3,
            ambienteDestino = "Ambiente TIC",
            proposito = "Practica de laboratorio",
            duracionHoras = 2,
        )

        assertTrue(resultado.isFailure)
    }

    @Test
    fun cancelar_solicitada_libera_equipo() {
        val repository = InMemoryPrestamoRepository()
        val solicitud = repository.crearSolicitud(
            equipoId = 2,
            ambienteDestino = "Ambiente TIC",
            proposito = "Practica de laboratorio",
            duracionHoras = 2
        ).getOrThrow()

        val resultado = repository.cancelarSolicitud(solicitud.id)

        assertTrue(resultado.isSuccess)
        assertEquals(EstadoSolicitud.CANCELADA, repository.obtenerSolicitud(solicitud.id)?.estado)
        assertEquals(EstadoEquipo.DISPONIBLE, repository.obtenerEquipo(2)?.estado)
    }
}
