package com.example.prestamolabctma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestamolabctma.data.repository.PrestamoRepository
import com.example.prestamolabctma.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PrestamoViewModel(
    private val repository: PrestamoRepository,
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
                solicitudes = repository.obtenerSolicitudes(),
                usuarioLogueado = repository.obtenerUsuarioLogueado(),
            )
        }
        verificarAlertas()
    }

    // HU-14: Notificaciones y Alertas de Vencimiento
    private fun verificarAlertas() {
        val ahora = System.currentTimeMillis()
        val quinceMinutos = 15 * 60 * 1000L
        
        val porVencer = mutableListOf<Int>()
        val vencidas = mutableListOf<Int>()
        
        _uiState.value.solicitudes.forEach { sol ->
            if (sol.estado == EstadoSolicitud.ENTREGADA) {
                val finPrestamo = sol.fechaSolicitud + (sol.duracionHoras * 60 * 60 * 1000L)
                val tiempoRestante = finPrestamo - ahora
                
                if ((tiempoRestante in 0..quinceMinutos)) {
                    porVencer.add(sol.id)
                } else if (tiempoRestante < 0) {
                    vencidas.add(sol.id)
                }
            }
        }
        
        _uiState.update { 
            it.copy(
                solicitudesPorVencer = porVencer,
                solicitudesVencidas = vencidas,
            )
        }
    }

    // HU-09: Registro de Nuevos Equipos
    fun registrarNuevoEquipo(
        nombre: String,
        serie: String,
        categoria: CategoriaEquipo,
        ubicacion: String,
        marca: String,
        specs: String,
        accesorios: String,
        url: String,
    ) {
        if (nombre.isBlank() || serie.isBlank()) {
            _uiState.update { it.copy(errorFormulario = "Nombre y Serie son obligatorios") }
            return
        }

        val nuevo = Equipo(
            id = 0,
            nombre = nombre,
            numSerie = serie,
            categoria = categoria,
            ubicacion = ubicacion,
            marca = marca,
            especificaciones = specs,
            accesorios = accesorios,
            imageUrl = url,
            estado = EstadoEquipo.DISPONIBLE
        )

        viewModelScope.launch {
            repository.registrarEquipo(nuevo).onSuccess {
                cargarDatos()
                _uiState.update { it.copy(mensaje = "Equipo registrado con éxito") }
            }.onFailure { error ->
                _uiState.update { it.copy(errorFormulario = error.message) }
            }
        }
    }

    // HU-10: Actualización del Estado de Equipos
    fun cambiarEstadoOperativo(equipoId: Int, nuevoEstado: EstadoEquipo) {
        viewModelScope.launch {
            repository.actualizarEstadoEquipo(equipoId, nuevoEstado).onSuccess {
                cargarDatos()
            }
        }
    }

    // HU-11: Aprobación y Rechazo de Solicitudes
    fun aprobarSolicitud(id: Int) {
        viewModelScope.launch {
            repository.aprobarSolicitud(id).onSuccess {
                cargarDatos()
                _uiState.update { it.copy(mensaje = "Solicitud Aprobada") }
            }
        }
    }

    fun rechazarSolicitud(id: Int, justificacion: String) {
        if (justificacion.isBlank()) {
            _uiState.update { it.copy(mensaje = "La justificación es obligatoria") }
            return
        }
        viewModelScope.launch {
            repository.rechazarSolicitud(id, justificacion).onSuccess {
                cargarDatos()
                _uiState.update { it.copy(mensaje = "Solicitud Rechazada") }
            }
        }
    }

    // HU-12 y HU-13: Devolución y Novedades
    fun registrarRetorno(id: Int, novedad: String?, gravedad: NivelGravedad?) {
        viewModelScope.launch {
            repository.registrarDevolucion(id, novedad, gravedad).onSuccess {
                cargarDatos()
                _uiState.update { it.copy(mensaje = "Devolución registrada") }
            }
        }
    }

    // HU-15: Estadísticas
    fun cargarEstadisticas() {
        val stats = repository.obtenerEstadisticas()
        _uiState.update { it.copy(estadisticas = stats) }
    }

    // HU-16: Trazabilidad
    fun cargarTrazabilidad(equipoId: Int) {
        val historial = repository.obtenerTrazabilidad(equipoId)
        _uiState.update { it.copy(trazabilidadEquipo = historial) }
    }

    // HU-05: Inicio de Sesión Institucional
    fun login(correo: String) {
        val regexSena = Regex("^[A-Za-z0-9._%+-]+@(soy\\.)?sena\\.edu\\.co$")
        
        if (!regexSena.matches(correo)) {
            _uiState.update { it.copy(errorFormulario = "Error: Use un correo @soy.sena.edu.co o @sena.edu.co") }
            return
        }

        _uiState.update { it.copy(guardando = true, errorFormulario = null) }
        
        viewModelScope.launch {
            repository.login(correo).onSuccess { usuario ->
                _uiState.update { it.copy(usuarioLogueado = usuario, guardando = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(errorFormulario = error.message, guardando = false) }
            }
        }
    }

    fun logout() {
        // En una implementación real limpiaríamos el repositorio
        _uiState.update { it.copy(usuarioLogueado = null) }
    }

    // HU-06: Gestión de Perfil
    fun actualizarPerfil(telefono: String, correoAlt: String) {
        val usuarioActual = _uiState.value.usuarioLogueado ?: return
        
        val usuarioActualizado = usuarioActual.copy(
            telefono = telefono,
            correoAlternativo = correoAlt,
        )

        viewModelScope.launch {
            repository.actualizarPerfil(usuarioActualizado).onSuccess {
                _uiState.update { it.copy(usuarioLogueado = usuarioActualizado, mensaje = "Perfil actualizado") }
            }
        }
    }

    // HU-07: Búsqueda y Filtros
    fun actualizarBusqueda(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun seleccionarCategoria(categoria: CategoriaEquipo?) {
        _uiState.update { it.copy(categoriaSeleccionada = categoria) }
    }

    val equiposFiltrados: StateFlow<List<Equipo>> = _uiState
        .map { state ->
            state.equipos.filter { equipo ->
                val matchNombre = equipo.nombre.contains(state.searchQuery, ignoreCase = true)
                val matchCategoria = (state.categoriaSeleccionada == null) || (equipo.categoria == state.categoriaSeleccionada)
                matchNombre && matchCategoria
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // HU-08: Marcado de Equipos Frecuentes (Favoritos)
    fun toggleFavorito(equipoId: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(equipoId).onSuccess {
                cargarDatos()
            }
        }
    }

    fun registrarSolicitud(
        equipoId: Int,
        ambiente: String,
        proposito: String,
        duracion: Int,
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
        if (duracion !in 1..8) {
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
                estado = EstadoSolicitud.SOLICITADA,
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
