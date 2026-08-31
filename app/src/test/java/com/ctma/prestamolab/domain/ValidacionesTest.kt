package com.ctma.prestamolab.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesTest {
    @Test
    fun proposito_respeta_limites_de_la_guia() {
        assertFalse(propositoValido("123456789"))
        assertTrue(propositoValido("1234567890"))
        assertTrue(propositoValido("a".repeat(180)))
        assertFalse(propositoValido("a".repeat(181)))
    }

    @Test
    fun duracion_respeta_limites_de_la_guia() {
        assertFalse(duracionValida(0))
        assertTrue(duracionValida(1))
        assertTrue(duracionValida(8))
        assertFalse(duracionValida(9))
    }
}
