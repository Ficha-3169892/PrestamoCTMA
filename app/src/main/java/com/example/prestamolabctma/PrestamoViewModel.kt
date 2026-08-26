package com.example.prestamolabctma

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

class PrestamoViewModel(private val repository: PrestamoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val equipos = repository.obtenerEquipos()
                val solicitudes = repository.obtenerSolicitudes()
                _uiState.update { 
                    it.copy(equipos = equipos, solicitudes = solicitudes, isLoading = false) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(errorMessage = "Error al cargar datos: ${e.message}", isLoading = false) 
                }
            }
        }
    }

    fun crearSolicitud(
        equipo: Equipo,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ) {
        if (_uiState.value.isLoading) return

        if (ambienteDestino.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El ambiente de destino no puede estar vacío") }
            return
        }

        if (proposito.length !in 10..180) {
            _uiState.update { it.copy(errorMessage = "El propósito debe tener entre 10 y 180 caracteres") }
            return
        }

        if (duracionHoras !in 1..8) {
            _uiState.update { it.copy(errorMessage = "La duración debe ser entre 1 y 8 horas") }
            return
        }

        if (equipo.estado != EstadoEquipo.DISPONIBLE) {
            _uiState.update { it.copy(errorMessage = "El equipo no está disponible") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val nuevaSolicitud = SolicitudPrestamo(
                id = (_uiState.value.solicitudes.maxOfOrNull { it.id } ?: 0) + 1,
                equipoId = equipo.id,
                ambienteDestino = ambienteDestino,
                proposito = proposito,
                duracionHoras = duracionHoras,
                estado = EstadoSolicitud.SOLICITADA
            )

            val resultado = repository.crearSolicitud(nuevaSolicitud)
            
            if (resultado.isSuccess) {
                cargarDatos()
            } else {
                _uiState.update { 
                    it.copy(errorMessage = resultado.exceptionOrNull()?.message, isLoading = false) 
                }
            }
        }
    }

    fun cancelarSolicitud(solicitud: SolicitudPrestamo) {
        if (_uiState.value.isLoading) return

        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            _uiState.update { it.copy(errorMessage = "Solo se pueden cancelar solicitudes pendientes (SOLICITADA)") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val resultado = repository.cancelarSolicitud(solicitud.id)
            if (resultado.isSuccess) {
                cargarDatos()
            } else {
                _uiState.update { 
                    it.copy(errorMessage = resultado.exceptionOrNull()?.message, isLoading = false) 
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

class PrestamoViewModelFactory(private val repository: PrestamoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrestamoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PrestamoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
