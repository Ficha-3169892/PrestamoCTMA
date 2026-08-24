package com.ctma.prestamolab.ui.viewmodel

import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val equipoSeleccionado: Equipo? = null,
    val mensajeError: String? = null,
    val mensajeExito: String? = null,
    val guardando: Boolean = false // RN-05: Controla que no se envíe dos veces
)