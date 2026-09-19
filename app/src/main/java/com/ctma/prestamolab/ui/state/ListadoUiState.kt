package com.ctma.prestamolab.ui.state

/**
 * [HU General] Molde técnico para la gestión de estados reactivos en listados.
 * Sigue el estándar de arquitectura propuesto para la Semana 7.
 */
sealed interface ListadoUiState<out T> {
    data object Cargando : ListadoUiState<Nothing>
    data object Vacio : ListadoUiState<Nothing>
    data class Contenido<T>(val datos: List<T>) : ListadoUiState<T>
    data class Error(val mensaje: String) : ListadoUiState<Nothing>
}
