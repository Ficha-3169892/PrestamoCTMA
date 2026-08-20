package com.example.prestamolabctma.navigation

sealed class Screen(val route: String) {
    data object Catalogo : Screen("catalogo")
    
    data object EquipoDetalle : Screen("equipo/{equipoId}") {
        fun createRoute(equipoId: Int) = "equipo/$equipoId"
    }
    
    data object SolicitudForm : Screen("solicitud/{equipoId}") {
        fun createRoute(equipoId: Int) = "solicitud/$equipoId"
    }
    
    data object MisPrestamos : Screen("misprestamos")
    
    @Suppress("unused")
    data object SolicitudDetalle : Screen("detalle_solicitud/{solicitudId}") {
        @Suppress("unused")
        fun createRoute(solicitudId: Int) = "detalle_solicitud/$solicitudId"
    }
}
