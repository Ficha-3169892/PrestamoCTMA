package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.model.Usuario
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AuthRepository {
    val usuarioLogueado: StateFlow<Usuario?>
    
    fun iniciarSesion(correo: String, contrasena: String): Result<Usuario>
    fun cerrarSesion()
    fun actualizarContacto(telefono: String, correoAlternativo: String?): Result<Unit>
}

class InMemoryAuthRepository : AuthRepository {
    private val _usuarioLogueado = kotlinx.coroutines.flow.MutableStateFlow<Usuario?>(null)
    override val usuarioLogueado = _usuarioLogueado.asStateFlow()

    override fun iniciarSesion(correo: String, contrasena: String): Result<Usuario> {
        val esValido = correo.endsWith("@soy.sena.edu.co") || correo.endsWith("@sena.edu.co")
        if (!esValido) {
            return Result.failure(IllegalArgumentException("El correo debe ser institucional (@sena.edu.co o @soy.sena.edu.co)"))
        }
        
        // Simulación de usuario
        val usuario = Usuario(
            id = 100,
            nombre = "Aprendiz de Pruebas",
            documento = "123456789",
            ficha = "2559000",
            telefono = "3001234567",
            correoInstitucional = correo,
            esAdministrador = correo.contains("admin"),
        )
        _usuarioLogueado.value = usuario
        return Result.success(usuario)
    }

    override fun cerrarSesion() {
        _usuarioLogueado.value = null
    }

    override fun actualizarContacto(telefono: String, correoAlternativo: String?): Result<Unit> {
        val actual = _usuarioLogueado.value ?: return Result.failure(IllegalStateException("No hay sesión activa"))
        _usuarioLogueado.value = actual.copy(telefono = telefono, correoAlternativo = correoAlternativo)
        return Result.success(Unit)
    }
}
