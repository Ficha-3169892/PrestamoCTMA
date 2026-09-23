package com.ctma.prestamolab.domain

import org.junit.Assert.*
import org.junit.Test

/**
 * [HU 05] Pruebas unitarias para las validaciones de acceso SENA.
 */
class LoginValidacionTest {

    @Test
    fun `correoSenaValido debe aceptar dominios institucionales`() {
        assertTrue(correoSenaValido("aprendiz@soy.sena.edu.co"))
        assertTrue(correoSenaValido("instructor@misena.edu.co"))
        assertTrue(correoSenaValido("admin.ctma@misena.edu.co"))
    }

    @Test
    fun `correoSenaValido debe rechazar dominios externos`() {
        assertFalse(correoSenaValido("usuario@gmail.com"))
        assertFalse(correoSenaValido("usuario@sena.edu.co")) // Falta el subdominio soy o misena
        assertFalse(correoSenaValido("usuario@outlook.es"))
    }

    @Test
    fun `contrasenaValida debe exigir al menos 6 caracteres`() {
        assertTrue(contrasenaValida("123456"))
        assertTrue(contrasenaValida("contraseñaSegura"))
        assertFalse(contrasenaValida("12345"))
    }

    @Test
    fun `validarLogin debe reportar errores correctamente`() {
        val errores = validarLogin("invalido@gmail.com", "123")
        assertTrue(errores.hayErrores)
        assertNotNull(errores.correo)
        assertNotNull(errores.contrasena)
    }
}
