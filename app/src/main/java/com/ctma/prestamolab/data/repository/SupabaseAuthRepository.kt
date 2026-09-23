package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.data.local.dao.UsuarioDao
import com.ctma.prestamolab.data.local.entity.UsuarioEntity
import com.ctma.prestamolab.model.Usuario
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * [HU 05/06] Implementación real de autenticación con Supabase Auth (Semana 08).
 * Soporta persistencia híbrida guardando el perfil en Room.
 */
class SupabaseAuthRepository(
    private val supabase: SupabaseClient,
    private val usuarioDao: UsuarioDao
) : AuthRepository {

    private val _usuarioLogueado = MutableStateFlow<Usuario?>(null)
    override val usuarioLogueado: StateFlow<Usuario?> = _usuarioLogueado.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        // Intentar recuperar sesión local al iniciar
        scope.launch {
            val localUser = usuarioDao.obtenerTodos().first().firstOrNull()
            if (localUser != null) {
                _usuarioLogueado.value = localUser.toDomain()
            }
        }
    }

    override suspend fun iniciarSesion(correo: String, contrasena: String): Result<Usuario> {
        // [MODO DE PRUEBA] Bypass para saltar Supabase si no está configurado o para testeo rápido
        if (contrasena == "123456" || supabase.supabaseUrl.contains("YOUR_PROJECT_URL")) {
            val usuarioMock = Usuario(
                id = correo.hashCode(),
                nombre = if (correo.contains("admin")) "Admin de Pruebas" else "Aprendiz de Pruebas",
                documento = "1000000000",
                ficha = "2559000",
                telefono = "3100000000",
                correoInstitucional = correo,
                esAdministrador = correo.contains("admin")
            )
            usuarioDao.borrarTodos()
            usuarioDao.insertar(usuarioMock.toEntity())
            _usuarioLogueado.value = usuarioMock
            return Result.success(usuarioMock)
        }

        return try {
            supabase.auth.signInWith(Email) {
                email = correo
                password = contrasena
            }
            
            // Simulación: En un caso real, aquí descargaríamos el perfil de una tabla 'perfiles'
            // Por ahora mapeamos los datos básicos del usuario de Supabase
            val sbUser = supabase.auth.currentUserOrNull() ?: throw Exception("Error al obtener usuario")
            
            val usuario = Usuario(
                id = sbUser.id.hashCode(), // Simplificación para el MVP
                nombre = sbUser.userMetadata?.get("nombre")?.toString() ?: "Usuario Supabase",
                documento = "N/A",
                ficha = "N/A",
                telefono = "N/A",
                correoInstitucional = correo,
                esAdministrador = correo.contains("admin")
            )
            
            // Guardar localmente (SSOT)
            usuarioDao.borrarTodos()
            usuarioDao.insertar(usuario.toEntity())
            
            _usuarioLogueado.value = usuario
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cerrarSesion() {
        try {
            supabase.auth.signOut()
            usuarioDao.borrarTodos()
            _usuarioLogueado.value = null
        } catch (e: Exception) {
            // Log error
        }
    }

    override suspend fun actualizarContacto(telefono: String, correoAlternativo: String?): Result<Unit> {
        val actual = _usuarioLogueado.value ?: return Result.failure(Exception("No hay sesión"))
        val actualizado = actual.copy(telefono = telefono, correoAlternativo = correoAlternativo)
        
        return try {
            // Actualizar localmente primero (ADN Resiliente)
            usuarioDao.insertar(actualizado.toEntity())
            _usuarioLogueado.value = actualizado
            
            // TODO: Sincronizar con Supabase en segundo plano (HU-06)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Mappers
    private fun UsuarioEntity.toDomain() = Usuario(
        id = id,
        nombre = nombre,
        documento = documento,
        ficha = ficha,
        telefono = telefono,
        correoInstitucional = correoInstitucional,
        correoAlternativo = correoAlternativo,
        esAdministrador = esAdministrador
    )

    private fun Usuario.toEntity() = UsuarioEntity(
        id = id,
        nombre = nombre,
        documento = documento,
        ficha = ficha,
        telefono = telefono,
        correoInstitucional = correoInstitucional,
        correoAlternativo = correoAlternativo,
        esAdministrador = esAdministrador
    )
}
