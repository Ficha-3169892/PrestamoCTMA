package com.ctma.prestamolab.ui.state

import com.ctma.prestamolab.domain.ErroresSolicitud
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.model.SolicitudPrestamo

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val mensaje: String? = null,
    val guardando: Boolean = false,
    val erroresSolicitud: ErroresSolicitud = ErroresSolicitud(),
    val busqueda: String = "",
    val categoriaSeleccionada: CategoriaEquipo? = null,
    val estadisticas: Map<String, Int> = emptyMap(),
    val trazabilidad: List<SolicitudPrestamo> = emptyList(),
) {
    val equiposFiltrados: List<Equipo>
        get() = equipos.filter { equipo ->
            val esPublico = (equipo.estado != EstadoEquipo.MANTENIMIENTO) && 
                            (equipo.estado != EstadoEquipo.FUERA_DE_SERVICIO)
            val coincideBusqueda = equipo.nombre.contains(busqueda, ignoreCase = true) ||
                    equipo.serie.contains(busqueda, ignoreCase = true) ||
                    equipo.marca.contains(busqueda, ignoreCase = true)
            val coincideCategoria = categoriaSeleccionada == null || equipo.categoria == categoriaSeleccionada
            esPublico && coincideBusqueda && coincideCategoria
        }
}
