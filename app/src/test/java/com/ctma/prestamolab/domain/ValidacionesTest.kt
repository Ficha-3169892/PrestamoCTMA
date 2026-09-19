package com.ctma.prestamolab.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [HU 02] Pruebas para las reglas de negocio de validación de préstamos.
 */
class ValidacionesTest {

    @Test
    fun test_HU_02_Ambiente_destino_no_vacio() {
        // [HU 02] Regla de negocio: Ambiente no vacío
        assertFalse(ambienteValido(""))
        assertFalse(ambienteValido("   "))
        assertTrue(ambienteValido("Laboratorio 1"))
    }

    @Test
    fun test_HU_02_Proposito_longitud_valida() {
        // [HU 02] Regla de negocio: Propósito (10-180 chars)
        // Arrange
        val corto = "Corto"
        val justo = "Práctica de electrónica básica"
        val largo = "a".repeat(181)

        // Act & Assert
        assertFalse(propositoValido(corto))
        assertTrue(propositoValido(justo))
        assertFalse(propositoValido(largo))
    }

    @Test
    fun test_HU_02_Duracion_rango_valido() {
        // [HU 02] Regla de negocio: Duración (1-8 horas)
        assertFalse(duracionValida(0))
        assertTrue(duracionValida(1))
        assertTrue(duracionValida(8))
        assertFalse(duracionValida(9))
    }
}
