package com.example.prestamolabctma.viewmodel

import com.example.prestamolabctma.model.*

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val usuarioLogueado: Usuario? = null,
    val searchQuery: String = "",
    val categoriaSeleccionada: CategoriaEquipo? = null,
    val mensaje: String? = null,
    val guardando: Boolean = false,
    val errorFormulario: String? = null,
    
    // Admin state
    val estadisticas: ReporteEstadistico? = null,
    val trazabilidadEquipo: List<SolicitudPrestamo> = emptyList(),
    
    // Alertas (HU-14)
    val solicitudesPorVencer: List<Int> = emptyList(), // IDs de solicitudes a 15 min de vencer
    val solicitudesVencidas: List<Int> = emptyList(), // IDs de solicitudes vencidas
)
