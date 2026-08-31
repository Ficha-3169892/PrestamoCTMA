package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo

interface PrestamoRepository {
    fun obtenerEquipos(): List<Equipo>
    fun obtenerEquipo(id: Int): Equipo?
    fun obtenerSolicitudes(): List<SolicitudPrestamo>
    fun obtenerSolicitud(id: Int): SolicitudPrestamo?
    fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int,
    ): Result<SolicitudPrestamo>
    fun cancelarSolicitud(id: Int): Result<Unit>
    fun conmutarFavorito(equipoId: Int): Result<Unit>
    fun agregarEquipo(equipo: Equipo): Result<Unit>
    fun aprobarSolicitud(id: Int): Result<Unit>
    fun rechazarSolicitud(id: Int, justificacion: String): Result<Unit>
    fun devolverEquipo(solicitudId: Int, novedades: String?): Result<Unit>
    fun obtenerEstadisticas(): Map<String, Int>
    fun obtenerTrazabilidad(equipoId: Int): List<SolicitudPrestamo>
}
