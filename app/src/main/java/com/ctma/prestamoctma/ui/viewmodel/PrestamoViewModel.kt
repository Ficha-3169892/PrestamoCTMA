package com.ctma.prestamoctma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ctma.prestamoctma.PrestamoApplication
import com.ctma.prestamoctma.data.local.datastore.PreferenciasRepository
import com.ctma.prestamoctma.data.local.entities.EvidenciaEntity
import com.ctma.prestamoctma.data.local.entities.UploadStatus
import com.ctma.prestamoctma.data.repository.PrestamoRepository
import com.ctma.prestamoctma.model.*
import com.ctma.prestamoctma.util.Validaciones
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.ExperimentalCoroutinesApi

class PrestamoViewModel(
    private val repository: PrestamoRepository,
    private val preferenciasRepository: PreferenciasRepository,
) : ViewModel() {

    private val _operacionEstado = MutableStateFlow<OperacionUiState>(OperacionUiState.Idle)
    private val _errorMensaje = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _isRefreshing = MutableStateFlow(false)

    // RN-06: Fuente única de verdad reactiva con cancelación de búsquedas obsoletas.
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PrestamoUiState> = combine(
        repository.getEquipos(),
        _searchQuery,
        preferenciasRepository.filtroEstado,
        _operacionEstado,
        _errorMensaje,
        _isRefreshing,
        repository.getAllEvidencias()
    ) { flows ->
        val equipos = flows[0] as List<Equipo>
        val query = flows[1] as String
        val filtro = flows[2] as EstadoSolicitud?
        val operacion = flows[3] as OperacionUiState
        val error = flows[4] as String?
        val refreshing = flows[5] as Boolean
        val evidencias = flows[6] as List<EvidenciaEntity>
        
        val mapEvidencias = evidencias.groupBy { it.prestamoId }
        
        Triple(equipos, query, filtro) to (Triple(operacion, error, refreshing) to mapEvidencias)
    }.flatMapLatest { (data, statusPair) ->
        val (equipos, query, filtro) = data
        val (status, mapEvidencias) = statusPair
        val (operacion, error, refreshing) = status
        
        repository.getSolicitudes().map { solicitudes ->
            val filtradas = solicitudes.filter { sol ->
                (filtro == null || sol.estado == filtro) &&
                (query.isEmpty() || sol.nombreEquipo.contains(query, ignoreCase = true))
            }
            
            PrestamoUiState(
                listadoEquipos = if (equipos.isEmpty()) ListadoUiState.Vacio else ListadoUiState.Contenido(equipos),
                listadoSolicitudes = if (filtradas.isEmpty()) ListadoUiState.Vacio else ListadoUiState.Contenido(filtradas),
                evidenciasPorPrestamo = mapEvidencias,
                filtroEstado = filtro,
                searchQuery = query,
                isRefreshing = refreshing,
                operacionEstado = operacion,
                errorMensaje = error
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PrestamoUiState()
    )

    private val _vencimientosAlertados = mutableSetOf<String>()

    init {
        refreshData()
        startAlertPolling()
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refresh()
                .onFailure { e -> _errorMensaje.value = "Error de sincronización: ${e.message}" }
            _isRefreshing.value = false
        }
    }

    private fun startAlertPolling() {
        viewModelScope.launch {
            while (true) {
                val listado = uiState.value.listadoSolicitudes
                if (listado is ListadoUiState.Contenido) {
                    val porVencer = listado.items.filter {
                        it.faltaPocoParaVencer() && !_vencimientosAlertados.contains(it.id)
                    }

                    if (porVencer.isNotEmpty()) {
                        val nombres = porVencer.joinToString(", ") { it.nombreEquipo }
                        _errorMensaje.value = "Recordatorio: El préstamo de $nombres vence en menos de 15 minutos."
                        porVencer.forEach { _vencimientosAlertados.add(it.id) }
                    }
                }
                delay(1.minutes)
            }
        }
    }

    fun solicitarPrestamo(
        equipoId: String,
        ambiente: String,
        proposito: String,
        duracion: Int
    ) {
        if (_operacionEstado.value == OperacionUiState.Ejecutando) return

        viewModelScope.launch {
            _operacionEstado.value = OperacionUiState.Ejecutando
            _errorMensaje.value = null

            val equipo = repository.getEquipoById(equipoId)

            val validationError = when {
                equipo == null -> "Equipo no encontrado"
                !Validaciones.esEquipoDisponible(equipo.estado) -> "RN-04: El equipo no está disponible"
                !Validaciones.validarAmbienteDestino(ambiente) -> "RN-03: El ambiente es obligatorio"
                !Validaciones.validarProposito(proposito) -> "RN-01: El propósito debe tener entre 10 y 180 caracteres"
                !Validaciones.validarDuracion(duracion) -> "RN-02: La duración debe estar entre 1 y 8 horas"
                else -> null
            }

            if (validationError != null) {
                _operacionEstado.value = OperacionUiState.Fallo(validationError)
                _errorMensaje.value = validationError
                return@launch
            }

            val nuevaSolicitud = SolicitudPrestamo(
                id = UUID.randomUUID().toString(),
                equipoId = equipoId,
                nombreEquipo = equipo!!.nombre,
                usuarioId = "USER_CTMA_01",
                ambienteDestino = ambiente,
                proposito = proposito,
                duracionHoras = duracion
            )

            repository.registrarSolicitud(nuevaSolicitud)
                .onSuccess {
                    _operacionEstado.value = OperacionUiState.Exito
                }
                .onFailure { e ->
                    _operacionEstado.value = OperacionUiState.Fallo(e.message ?: "Error desconocido")
                    _errorMensaje.value = e.message
                }
        }
    }

    fun cancelarSolicitud(id: String) {
        viewModelScope.launch {
            _operacionEstado.value = OperacionUiState.Ejecutando
            repository.cancelarSolicitud(id)
                .onSuccess { 
                    _operacionEstado.value = OperacionUiState.Exito 
                    refreshData() // Refresca los datos para actualizar la UI inmediatamente
                }
                .onFailure { e ->
                    _operacionEstado.value = OperacionUiState.Fallo(e.message ?: "Error")
                    _errorMensaje.value = e.message
                }
        }
    }

    fun reportarNovedadDevolucion(
        solicitudId: String,
        detalle: String,
        gravedad: GravedadDano
    ) {
        viewModelScope.launch {
            _operacionEstado.value = OperacionUiState.Ejecutando
            repository.procesarDevolucion(solicitudId, detalle, gravedad)
                .onSuccess { 
                    _operacionEstado.value = OperacionUiState.Exito 
                    _errorMensaje.value = null
                }
                .onFailure { e ->
                    _operacionEstado.value = OperacionUiState.Fallo(e.message ?: "Error")
                    _errorMensaje.value = e.message
                }
        }
    }

    fun clearError() {
        _errorMensaje.value = null
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun resetOperacionEstado() {
        _operacionEstado.value = OperacionUiState.Idle
    }

    fun agregarEquipo(
        nombre: String,
        categoria: CategoriaEquipo,
        descripcion: String
    ) {
        if (nombre.isBlank()) {
            _errorMensaje.value = "El nombre es obligatorio"
            return
        }

        viewModelScope.launch {
            _operacionEstado.value = OperacionUiState.Ejecutando
            val nuevoEquipo = Equipo(
                id = UUID.randomUUID().toString(),
                nombre = nombre,
                categoria = categoria,
                estado = EstadoEquipo.DISPONIBLE,
                descripcion = descripcion
            )
            repository.agregarEquipo(nuevoEquipo)
                .onSuccess { _operacionEstado.value = OperacionUiState.Exito }
                .onFailure { e ->
                    _operacionEstado.value = OperacionUiState.Fallo(e.message ?: "Error")
                    _errorMensaje.value = e.message
                }
        }
    }

    fun guardarEvidencia(prestamoId: String, uri: String, size: Long, type: String) {
        // Validaciones: max 5MB, solo imágenes
        if (size > 5 * 1024 * 1024) {
            _errorMensaje.value = "La imagen no debe superar los 5MB"
            return
        }
        if (!type.startsWith("image/")) {
            _errorMensaje.value = "Solo se permiten imágenes"
            return
        }

        viewModelScope.launch {
            val evidencia = EvidenciaEntity(
                id = UUID.randomUUID().toString(),
                prestamoId = prestamoId,
                localUri = uri,
                mimeType = type,
                fileSizeBytes = size,
                status = UploadStatus.LOCAL
            )
            repository.guardarEvidenciaLocal(evidencia)
                .onSuccess {
                    repository.subirEvidencia(evidencia.id)
                }
        }
    }

    @Suppress("unused")
    fun actualizarFiltro(estado: EstadoSolicitud?) {
        viewModelScope.launch {
            preferenciasRepository.guardarFiltroEstado(estado)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as PrestamoApplication)
                PrestamoViewModel(
                    repository = application.container.prestamoRepository,
                    preferenciasRepository = application.container.preferenciasRepository,
                )
            }
        }
    }
}
