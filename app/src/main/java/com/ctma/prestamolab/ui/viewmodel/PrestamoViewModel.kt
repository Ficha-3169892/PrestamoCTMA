package com.ctma.prestamolab.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.ctma.prestamolab.data.ServiceLocator
import com.ctma.prestamolab.data.repository.PrestamoRepository
import com.ctma.prestamolab.domain.ErroresSolicitud
import com.ctma.prestamolab.domain.validarSolicitud
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo
import com.ctma.prestamolab.ui.state.PrestamoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PrestamoViewModel(
    private val repository: PrestamoRepository = ServiceLocator.repository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    init {
        refrescarDatos()
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
                    mensaje = "Revisa los campos marcados antes de guardar."
                )
            }
            return
        }

        _uiState.update { it.copy(guardando = true, erroresSolicitud = ErroresSolicitud()) }

        repository.crearSolicitud(
            equipoId = equipoId,
            ambienteDestino = ambienteDestino,
            proposito = proposito,
            duracionHoras = duracion ?: 0
        ).onSuccess { solicitud ->
            refrescarDatos("Solicitud registrada. El equipo quedó reservado.")
            alCrear(solicitud.id)
        }.onFailure { error ->
            refrescarDatos(error.message ?: "No fue posible crear la solicitud.")
        }

        _uiState.update { it.copy(guardando = false) }
    }

    fun cancelarSolicitud(id: Int) {
        repository.cancelarSolicitud(id)
            .onSuccess { refrescarDatos("Solicitud cancelada. La disponibilidad fue actualizada.") }
            .onFailure { error -> refrescarDatos(error.message ?: "No fue posible cancelar la solicitud.") }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }

    fun buscar(texto: String) {
        _uiState.update { it.copy(busqueda = texto) }
    }

    fun filtrarPorCategoria(categoria: CategoriaEquipo?) {
        _uiState.update { it.copy(categoriaSeleccionada = categoria) }
    }

    fun conmutarFavorito(equipoId: Int) {
        repository.conmutarFavorito(equipoId).onSuccess {
            refrescarDatos()
        }
    }

    fun agregarEquipo(equipo: Equipo, alTerminar: () -> Unit) {
        repository.agregarEquipo(equipo)
            .onSuccess {
                refrescarDatos("Equipo agregado al inventario.")
                alTerminar()
            }
            .onFailure { error ->
                refrescarDatos(error.message ?: "Error al agregar equipo.")
            }
    }

    fun aprobarSolicitud(id: Int) {
        repository.aprobarSolicitud(id)
            .onSuccess { refrescarDatos("Solicitud aprobada y equipo entregado.") }
            .onFailure { error -> refrescarDatos(error.message ?: "No se pudo aprobar.") }
    }

    fun rechazarSolicitud(id: Int, justificacion: String) {
        repository.rechazarSolicitud(id, justificacion)
            .onSuccess { refrescarDatos("Solicitud rechazada.") }
            .onFailure { error -> refrescarDatos(error.message ?: "No se pudo rechazar.") }
    }

    fun devolverEquipo(solicitudId: Int, novedades: String?) {
        repository.devolverEquipo(solicitudId, novedades)
            .onSuccess { refrescarDatos("Equipo devuelto y solicitud cerrada.") }
            .onFailure { error -> refrescarDatos(error.message ?: "Error en la devolución.") }
    }

    fun cargarTrazabilidad(equipoId: Int) {
        _uiState.update { it.copy(trazabilidad = repository.obtenerTrazabilidad(equipoId)) }
    }

    private fun refrescarDatos(mensaje: String? = uiState.value.mensaje) {
        _uiState.update {
            it.copy(
                equipos = repository.obtenerEquipos(),
                solicitudes = repository.obtenerSolicitudes(),
                estadisticas = repository.obtenerEstadisticas(),
                mensaje = mensaje
            )
        }
    }
}
