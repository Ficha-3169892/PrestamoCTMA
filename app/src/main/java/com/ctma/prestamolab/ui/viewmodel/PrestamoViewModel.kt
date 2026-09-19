package com.ctma.prestamolab.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ctma.prestamolab.data.ServiceLocator
import com.ctma.prestamolab.data.repository.PrestamoRepository
import com.ctma.prestamolab.domain.validarSolicitud
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo
import com.ctma.prestamolab.ui.state.ListadoUiState
import com.ctma.prestamolab.ui.state.PrestamoUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * [HU General] ViewModel unificado para la gestión de préstamos.
 * Implementa Estado Reactivo y Búsqueda Pro (Semana 07).
 */
class PrestamoViewModel(
    private val repository: PrestamoRepository = ServiceLocator.repository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    init {
        // [Semana 07] Recolección reactiva de datos desde SSOT con Búsqueda Pro
        observarEquiposPro()
        observarSolicitudes()
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun observarEquiposPro() {
        // [Semana 07] Búsqueda Pro: flatMapLatest para cancelar consultas obsoletas
        _uiState
            .map { it.busqueda }
            .distinctUntilChanged()
            .debounce(300)
            .flatMapLatest { query -> repository.observarEquipos(query) }
            .onEach { equipos ->
                _uiState.update { 
                    it.copy(equiposState = if (equipos.isEmpty()) ListadoUiState.Vacio else ListadoUiState.Contenido(equipos))
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observarSolicitudes() {
        repository.observarSolicitudes()
            .onEach { solicitudes ->
                _uiState.update { 
                    it.copy(solicitudesState = if (solicitudes.isEmpty()) ListadoUiState.Vacio else ListadoUiState.Contenido(solicitudes))
                }
            }
            .launchIn(viewModelScope)
    }

    fun obtenerEquipo(id: Int): Equipo? = uiState.value.equipos.firstOrNull { it.id == id }

    fun obtenerSolicitud(id: Int): SolicitudPrestamo? =
        uiState.value.solicitudes.firstOrNull { it.id == id }

    fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionTexto: String,
        alCrear: (Int) -> Unit,
    ) {
        if (uiState.value.guardando) return

        val duracion = duracionTexto.toIntOrNull()
        val errores = validarSolicitud(ambienteDestino, proposito, duracion)
        if (errores.hayErrores) {
            _uiState.update {
                it.copy(
                    erroresSolicitud = errores,
                    mensaje = "Revisa los campos marcados antes de guardar.",
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true) }
            repository.crearSolicitud(equipoId, ambienteDestino, proposito, duracion ?: 0)
                .onSuccess { id ->
                    _uiState.update { it.copy(mensaje = "Solicitud enviada con éxito", guardando = false) }
                    alCrear(id.toInt())
                }
                .onFailure { error ->
                    _uiState.update { it.copy(mensaje = error.message, guardando = false) }
                }
        }
    }

    fun conmutarFavorito(equipoId: Int) {
        viewModelScope.launch {
            repository.conmutarFavorito(equipoId)
            // No es necesario refrescar manualmente, el Flow observador lo hará automáticamente (SSOT)
        }
    }

    fun buscar(texto: String) {
        _uiState.update { it.copy(busqueda = texto) }
    }

    fun filtrarPorCategoria(categoria: CategoriaEquipo?) {
        _uiState.update { it.copy(categoriaSeleccionada = categoria) }
    }

    fun agregarEquipo(equipo: Equipo, alTerminar: () -> Unit) {
        viewModelScope.launch {
            repository.agregarEquipo(equipo)
                .onSuccess { alTerminar() }
        }
    }

    fun aprobarSolicitud(id: Int) {
        viewModelScope.launch { repository.aprobarSolicitud(id) }
    }

    fun rechazarSolicitud(id: Int, justificacion: String) {
        viewModelScope.launch { repository.rechazarSolicitud(id, justificacion) }
    }

    fun devolverEquipo(solicitudId: Int, novedades: String?) {
        viewModelScope.launch { repository.devolverEquipo(solicitudId, novedades) }
    }

    fun cargarTrazabilidad(equipoId: Int) {
        repository.observarTrazabilidad(equipoId)
            .onEach { trazabilidad ->
                _uiState.update { 
                    it.copy(trazabilidadState = if (trazabilidad.isEmpty()) ListadoUiState.Vacio else ListadoUiState.Contenido(trazabilidad))
                }
            }
            .launchIn(viewModelScope)
    }

    fun cancelarSolicitud(id: Int) {
        viewModelScope.launch {
            repository.cancelarSolicitud(id)
                .onFailure { error -> _uiState.update { it.copy(mensaje = error.message) } }
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }
}
