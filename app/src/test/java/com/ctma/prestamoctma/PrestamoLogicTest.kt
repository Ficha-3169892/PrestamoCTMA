package com.ctma.prestamoctma

import com.ctma.prestamoctma.model.EstadoEquipo
import com.ctma.prestamoctma.model.EstadoSolicitud
import com.ctma.prestamoctma.util.Validaciones
import org.junit.Assert.*
import org.junit.Test

class PrestamoLogicTest {

    // --- PRUEBAS DE VALIDACIÓN (REGLAS DE NEGOCIO) ---

    @Test
    fun `TC-01 Proposito demasiado corto debe fallar`() {
        assertFalse(Validaciones.validarProposito("Corto"))
    }

    @Test
    fun `TC-02 Proposito dentro del rango debe pasar`() {
        assertTrue(Validaciones.validarProposito("Solicitud de préstamo para clase de diseño"))
    }

    @Test
    fun `TC-03 Proposito demasiado largo debe fallar`() {
        val largo = "a".repeat(181)
        assertFalse(Validaciones.validarProposito(largo))
    }

    @Test
    fun `TC-04 Duracion minima de 1 hora debe pasar`() {
        assertTrue(Validaciones.validarDuracion(1))
    }

    @Test
    fun `TC-05 Duracion maxima de 8 horas debe pasar`() {
        assertTrue(Validaciones.validarDuracion(8))
    }

    @Test
    fun `TC-06 Duracion fuera de rango debe fallar`() {
        assertFalse(Validaciones.validarDuracion(0))
        assertFalse(Validaciones.validarDuracion(9))
    }

    @Test
    fun `TC-07 Ambiente vacio debe fallar`() {
        assertFalse(Validaciones.validarAmbienteDestino("   "))
    }

    @Test
    fun `TC-08 Ambiente con texto debe pasar`() {
        assertTrue(Validaciones.validarAmbienteDestino("Ambiente 204"))
    }

    // --- PRUEBAS DE ESTADO DE EQUIPO ---

    @Test
    fun `TC-09 Equipo DISPONIBLE permite prestamo`() {
        assertTrue(Validaciones.esEquipoDisponible(EstadoEquipo.DISPONIBLE))
    }

    @Test
    fun `TC-10 Equipo PRESTADO o RESERVADO no permite prestamo`() {
        assertFalse(Validaciones.esEquipoDisponible(EstadoEquipo.PRESTADO))
        assertFalse(Validaciones.esEquipoDisponible(EstadoEquipo.RESERVADO))
    }

    // --- PRUEBAS DE TRANSICIÓN DE SOLICITUD ---

    @Test
    fun `TC-11 Transicion SOLICITADA a CANCELADA es permitida`() {
        assertTrue(Validaciones.esTransicionPermitida(EstadoSolicitud.SOLICITADA, EstadoSolicitud.CANCELADA))
    }

    @Test
    fun `TC-12 Transicion de CANCELADA a cualquier otra debe fallar`() {
        assertFalse(Validaciones.esTransicionPermitida(EstadoSolicitud.CANCELADA, EstadoSolicitud.APROBADA))
    }
}
