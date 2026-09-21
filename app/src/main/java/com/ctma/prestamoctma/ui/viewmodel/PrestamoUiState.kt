package com.ctma.prestamoctma.ui.viewmodel

import com.ctma.prestamoctma.model.Equipo
import com.ctma.prestamoctma.model.EstadoSolicitud
import com.ctma.prestamoctma.model.SolicitudPrestamo

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val filtroEstado: EstadoSolicitud? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSolicitudExitosa: Boolean = false,
    val isEquipoAgregadoExitosamente: Boolean = false
)
