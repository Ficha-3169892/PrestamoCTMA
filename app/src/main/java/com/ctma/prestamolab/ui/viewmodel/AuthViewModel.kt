package com.ctma.prestamolab.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ctma.prestamolab.data.ServiceLocator
import com.ctma.prestamolab.data.repository.AuthRepository
import com.ctma.prestamolab.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val usuario: Usuario? = null,
    val mensaje: String? = null,
    val cargando: Boolean = false,
)

class AuthViewModel(
    private val authRepository: AuthRepository = ServiceLocator.authRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.usuarioLogueado.collect { user ->
                _uiState.update { it.copy(usuario = user) }
            }
        }
    }

    fun iniciarSesion(correo: String, contrasena: String) {
        _uiState.update { it.copy(cargando = true) }
        authRepository.iniciarSesion(correo, contrasena)
            .onFailure { error ->
                _uiState.update { it.copy(mensaje = error.message, cargando = false) }
            }
            .onSuccess {
                _uiState.update { it.copy(mensaje = null, cargando = false) }
            }
    }

    fun cerrarSesion() {
        authRepository.cerrarSesion()
    }

    fun actualizarContacto(telefono: String, correoAlternativo: String?) {
        _uiState.update { it.copy(cargando = true) }
        authRepository.actualizarContacto(telefono, correoAlternativo)
            .onSuccess {
                _uiState.update { it.copy(mensaje = "Datos actualizados", cargando = false) }
            }
            .onFailure { error ->
                _uiState.update { it.copy(mensaje = error.message, cargando = false) }
            }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }
}
