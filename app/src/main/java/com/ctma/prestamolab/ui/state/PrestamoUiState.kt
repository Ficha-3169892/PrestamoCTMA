package com.ctma.prestamolab.ui.state

import com.ctma.prestamolab.domain.ErroresSolicitud
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.model.SolicitudPrestamo

data class PrestamoUiState(
    val equiposState: ListadoUiState<Equipo> = ListadoUiState.Cargando,
    val solicitudesState: ListadoUiState<SolicitudPrestamo> = ListadoUiState.Cargando,
    val trazabilidadState: ListadoUiState<SolicitudPrestamo> = ListadoUiState.Vacio,
    val mensaje: String? = null,
    val guardando: Boolean = false,
    val erroresSolicitud: ErroresSolicitud = ErroresSolicitud(),
    val busqueda: String = "",
    val categoriaSeleccionada: CategoriaEquipo? = null,
    val estadisticas: Map<String, Int> = emptyMap(),
) {
    // Para compatibilidad temporal con lógica existente durante la migración
    val equipos: List<Equipo>
        get() = (equiposState as? ListadoUiState.Contenido)?.datos ?: emptyList()

    val solicitudes: List<SolicitudPrestamo>
        get() = (solicitudesState as? ListadoUiState.Contenido)?.datos ?: emptyList()

    val trazabilidad: List<SolicitudPrestamo>
        get() = (trazabilidadState as? ListadoUiState.Contenido)?.datos ?: emptyList()
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
