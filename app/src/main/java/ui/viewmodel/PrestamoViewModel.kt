package com.ctma.prestamolab.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.ctma.prestamolab.data.repository.InMemoryPrestamoRepository
import com.ctma.prestamolab.data.repository.PrestamoRepository
import com.ctma.prestamolab.util.ambienteValido
import com.ctma.prestamolab.util.duracionValida
import com.ctma.prestamolab.util.propositoValido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PrestamoViewModel(
    private val repository: PrestamoRepository = InMemoryPrestamoRepository()
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

    fun seleccionarEquipo(id: Int) {
        val equipo = repository.obtenerEquipo(id)
        _uiState.update {
            it.copy(
                equipoSeleccionado = equipo,
                mensajeError = if (equipo == null) "El equipo solicitado no existe" else null
            )
        }
    }

    fun registrarSolicitud(equipoId: Int, ambiente: String, proposito: String, duracion: Int) {
        if (_uiState.value.guardando) return // RN-05: Bloquea doble Submit

        if (!ambienteValido(ambiente)) {
            _uiState.update { it.copy(mensajeError = "El ambiente destino es obligatorio") }
            return
        }
        if (!propositoValido(proposito)) {
            _uiState.update { it.copy(mensajeError = "El propósito debe tener entre 10 y 180 caracteres") }
            return
        }
        if (!duracionValida(duracion)) {
            _uiState.update { it.copy(mensajeError = "La duración debe ser entre 1 y 8 horas") }
            return
        }

        _uiState.update { it.copy(guardando = true, mensajeError = null) }

        val resultado = repository.crearSolicitud(equipoId, ambiente, proposito, duracion)

        resultado.onSuccess {
            cargarDatos()
            _uiState.update { state -> state.copy(guardando = false, mensajeExito = "Solicitud creada correctamente") }
        }.onFailure { error ->
            _uiState.update { state -> state.copy(guardando = false, mensajeError = error.message) }
        }
    }

    fun cancelarSolicitud(id: Int) {
        val resultado = repository.cancelarSolicitud(id)
        resultado.onSuccess {
            cargarDatos()
            _uiState.update { it.copy(mensajeExito = "Solicitud cancelada exitosamente") }
        }.onFailure { error ->
            _uiState.update { it.copy(mensajeError = error.message) }
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeError = null, mensajeExito = null) }
    }
}