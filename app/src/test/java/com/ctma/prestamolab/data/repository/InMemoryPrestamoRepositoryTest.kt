package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.model.EstadoSolicitud
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryPrestamoRepositoryTest {
    @Test
    fun test_HU_02_Crear_solicitud_reserva_equipo_disponible() = runTest {
        // [HU 02] Registrar Solicitud de Préstamo
        val repository = InMemoryPrestamoRepository()

        val resultado = repository.crearSolicitud(
            equipoId = 1,
            ambienteDestino = "Ambiente TIC",
            proposito = "Practica de laboratorio",
            duracionHoras = 2,
        )

        assertTrue(resultado.isSuccess)
        val solicitudId = resultado.getOrThrow()
        val solicitud = repository.obtenerSolicitud(solicitudId.toInt())
        assertEquals(EstadoSolicitud.SOLICITADA, solicitud?.estado)
        assertEquals(EstadoEquipo.RESERVADO, repository.obtenerEquipo(1)?.estado)
    }

    @Test
    fun test_HU_02_No_permite_solicitar_equipo_no_disponible() = runTest {
        // [HU 02] Regla: El equipo debe estar disponible
        val repository = InMemoryPrestamoRepository()
        // Equipo 99 no existe
        
        val resultado = repository.crearSolicitud(
            equipoId = 99,
            ambienteDestino = "Ambiente TIC",
            proposito = "Practica de laboratorio",
            duracionHoras = 2,
        )

        assertTrue(resultado.isFailure)
    }

    @Test
    fun test_HU_03_Cancelar_solicitada_libera_equipo() = runTest {
        // [HU 03] Gestión y Cancelación de Solicitudes
        val repository = InMemoryPrestamoRepository()
        val solicitudId = repository.crearSolicitud(
            equipoId = 1,
            ambienteDestino = "Ambiente TIC",
            proposito = "Practica de laboratorio con duracion",
            duracionHoras = 2
        ).getOrThrow().toInt()

        val resultado = repository.cancelarSolicitud(solicitudId)

        assertTrue(resultado.isSuccess)
        assertEquals(EstadoSolicitud.CANCELADA, repository.obtenerSolicitud(solicitudId)?.estado)
        assertEquals(EstadoEquipo.DISPONIBLE, repository.obtenerEquipo(1)?.estado)
    }
}
