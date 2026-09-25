package com.example.prestamolabctma.navigation

sealed class Screen(val route: String) {
    data object Catalogo : Screen("catalogo")
    data object Login : Screen("login")
    data object Perfil : Screen("perfil")
    data object AdminDashboard : Screen("admin_dashboard")
    data object RegistroEquipo : Screen("registro_equipo")
    data object GestionSolicitudes : Screen("gestion_solicitudes")
    data object Reportes : Screen("reportes")
    data object Trazabilidad : Screen("trazabilidad/{equipoId}") {
        fun createRoute(equipoId: Int) = "trazabilidad/$equipoId"
    }
    
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
