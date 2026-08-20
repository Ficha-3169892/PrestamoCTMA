package com.example.prestamolabctma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestamolabctma.data.repository.PrestamoRepository
import com.example.prestamolabctma.model.EstadoSolicitud
import com.example.prestamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PrestamoViewModel(
    private val repository: PrestamoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        _uiState.update { 
            it.copy(
                equipos = repository.obtenerEquipos(),
                solicitudes = repository.obtenerSolicitudes()
            )
        }
    }

    fun registrarSolicitud(
        equipoId: Int,
        ambiente: String,
        proposito: String,
        duracion: Int
    ) {
        // RN-05: Evitar duplicados por doble pulsación
        if (_uiState.value.guardando) return

        // Validaciones de negocio
        // RN-02: Ambiente obligatorio
        if (ambiente.isBlank()) {
            _uiState.update { it.copy(errorFormulario = "El ambiente o destino es obligatorio") }
            return
        }

        // RN-03: Propósito entre 10 y 180 caracteres
        if (proposito.length !in 10..180) {
            _uiState.update { it.copy(errorFormulario = "El propósito debe tener entre 10 y 180 caracteres") }
            return
        }

        // RN-04: Duración entre 1 y 8 horas
        if (duracion < 1 || duracion > 8) {
            _uiState.update { it.copy(errorFormulario = "La duración debe estar entre 1 y 8 horas") }
            return
        }

        _uiState.update { it.copy(guardando = true, errorFormulario = null) }

        viewModelScope.launch {
            val nuevaSolicitud = SolicitudPrestamo(
                id = (_uiState.value.solicitudes.size + 1),
                equipoId = equipoId,
                ambienteDestino = ambiente,
                proposito = proposito,
                duracionHoras = duracion,
                estado = EstadoSolicitud.SOLICITADA
            )

            val resultado = repository.crearSolicitud(nuevaSolicitud)
            
            resultado.onSuccess {
                _uiState.update { 
                    it.copy(
                        guardando = false,
                        mensaje = "Solicitud registrada con éxito",
                        equipos = repository.obtenerEquipos(),
                        solicitudes = repository.obtenerSolicitudes()
                    )
                }
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(
                        guardando = false,
                        errorFormulario = error.message ?: "Error al registrar solicitud"
                    )
                }
            }
        }
    }

    fun cancelarPrestamo(solicitudId: Int) {
        viewModelScope.launch {
            val resultado = repository.cancelarSolicitud(solicitudId)
            resultado.onSuccess {
                cargarDatos()
                _uiState.update { it.copy(mensaje = "Solicitud cancelada") }
            }.onFailure { error ->
                _uiState.update { it.copy(mensaje = error.message) }
            }
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }
}
