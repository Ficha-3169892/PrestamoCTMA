package com.ctma.prestamoctma.ui.viewmodel

import com.ctma.prestamoctma.model.Equipo
import com.ctma.prestamoctma.model.SolicitudPrestamo
import com.ctma.prestamoctma.model.EstadoSolicitud

/**
 * Estado general de la pantalla de préstamos.
 */
data class PrestamoUiState(
    val listadoSolicitudes: ListadoUiState<SolicitudPrestamo> = ListadoUiState.Cargando,
    val listadoEquipos: ListadoUiState<Equipo> = ListadoUiState.Cargando,
    val filtroEstado: EstadoSolicitud? = null,
    val searchQuery: String = "",
    val operacionEstado: OperacionUiState = OperacionUiState.Idle,
    val errorMensaje: String? = null
)

/**
 * Representa el estado de una lista de elementos (Equipos o Solicitudes).
 */
sealed interface ListadoUiState<out T> {
    data object Cargando : ListadoUiState<Nothing>
    data class Contenido<T>(val items: List<T>) : ListadoUiState<T>
    data object Vacio : ListadoUiState<Nothing>
    data class Error(val mensaje: String) : ListadoUiState<Nothing>
}

/**
 * Representa el estado de una operación de escritura (Crear, Cancelar, Devolver).
 */
sealed interface OperacionUiState {
    data object Idle : OperacionUiState
    data object Ejecutando : OperacionUiState
    data object Exito : OperacionUiState
    data class Fallo(val error: String) : OperacionUiState
}
