package com.ctma.prestamoctma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ctma.prestamoctma.data.repository.InMemoryPrestamoRepository
import com.ctma.prestamoctma.data.repository.PrestamoRepository
import com.ctma.prestamoctma.model.*
import com.ctma.prestamoctma.util.Validaciones
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class PrestamoViewModel(
    private val repository: PrestamoRepository = InMemoryPrestamoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    private val _vencimientosAlertados = mutableSetOf<String>()
    
    private var isSubmitting = false

    init {
        loadData()
        startAlertPolling()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            combine(
                repository.getEquipos(),
                repository.getSolicitudes()
            ) { equipos, solicitudes ->
                _uiState.update { 
                    it.copy(
                        equipos = equipos,
                        solicitudes = solicitudes,
                        isLoading = false
                    )
                }
            }.collect()
        }
    }

    private fun startAlertPolling() {
        viewModelScope.launch {
            while (true) {
                val solicitudes = _uiState.value.solicitudes
                val porVencer = solicitudes.filter { 
                    it.faltaPocoParaVencer() && !_vencimientosAlertados.contains(it.id) 
                }

                if (porVencer.isNotEmpty()) {
                    val nombres = porVencer.joinToString(", ") { it.nombreEquipo }
                    _uiState.update { it.copy(error = "Recordatorio: El préstamo de $nombres vence en menos de 15 minutos.") }
                    porVencer.forEach { _vencimientosAlertados.add(it.id) }
                }
                delay(60000) // Verificar cada minuto
            }
        }
    }

    fun solicitarPrestamo(
        equipoId: String,
        ambiente: String,
        proposito: String,
        duracion: Int
    ) {
        if (isSubmitting) return
        isSubmitting = true

        viewModelScope.launch {
            val equipo = repository.getEquipoById(equipoId)
            
            when {
                equipo == null -> {
                    _uiState.update { it.copy(error = "Equipo no encontrado") }
                }
                !Validaciones.esEquipoDisponible(equipo.estado) -> {
                    _uiState.update { it.copy(error = "RN-04: El equipo no está disponible") }
                }
                !Validaciones.validarAmbienteDestino(ambiente) -> {
                    _uiState.update { it.copy(error = "RN-03: El ambiente es obligatorio") }
                }
                !Validaciones.validarProposito(proposito) -> {
                    _uiState.update { it.copy(error = "RN-01: El propósito debe tener entre 10 y 180 caracteres") }
                }
                !Validaciones.validarDuracion(duracion) -> {
                    _uiState.update { it.copy(error = "RN-02: La duración debe estar entre 1 y 8 horas") }
                }
                else -> {
                    val nuevaSolicitud = SolicitudPrestamo(
                        id = UUID.randomUUID().toString(),
                        equipoId = equipoId,
                        nombreEquipo = equipo.nombre,
                        usuarioId = "USER_CTMA_01",
                        ambienteDestino = ambiente,
                        proposito = proposito,
                        duracionHoras = duracion
                    )
                    
                    repository.registrarSolicitud(nuevaSolicitud)
                        .onSuccess {
                            _uiState.update { it.copy(isSolicitudExitosa = true, error = null) }
                        }
                        .onFailure { e ->
                            _uiState.update { it.copy(error = e.message) }
                        }
                }
            }
            isSubmitting = false
        }
    }

    fun cancelarSolicitud(id: String) {
        viewModelScope.launch {
            repository.cancelarSolicitud(id)
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun reportarNovedadDevolucion(
        solicitudId: String,
        detalle: String,
        gravedad: GravedadDano
    ) {
        viewModelScope.launch {
            repository.procesarDevolucion(solicitudId, detalle, gravedad)
                .onSuccess {
                    _uiState.update { it.copy(error = null) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetSolicitudExitosa() {
        _uiState.update { it.copy(isSolicitudExitosa = false) }
    }
}
