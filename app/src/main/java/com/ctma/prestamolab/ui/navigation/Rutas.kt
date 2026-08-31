package com.ctma.prestamolab.ui.navigation

object Rutas {
    const val CATALOGO = "catalogo"
    const val MIS_SOLICITUDES = "mis_solicitudes"
    const val EQUIPO_DETALLE = "equipo_detalle/{equipoId}"
    const val SOLICITAR = "solicitar/{equipoId}"
    const val SOLICITUD_DETALLE = "solicitud_detalle/{solicitudId}"
    const val PERFIL = "perfil"
    const val LOGIN = "login"
    const val INVENTARIO = "inventario"
    const val GESTION_ADMIN = "gestion_admin"
    const val ESTADISTICAS = "estadisticas"
    const val TRAZABILIDAD = "trazabilidad/{equipoId}"

    fun equipoDetalle(equipoId: Int) = "equipo_detalle/$equipoId"
    fun solicitar(equipoId: Int) = "solicitar/$equipoId"
    fun solicitudDetalle(solicitudId: Int) = "solicitud_detalle/$solicitudId"
    fun trazabilidad(equipoId: Int) = "trazabilidad/$equipoId"
}
