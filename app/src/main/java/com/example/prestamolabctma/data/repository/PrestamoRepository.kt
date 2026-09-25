package com.example.prestamolabctma.data.repository

import com.example.prestamolabctma.model.*

interface PrestamoRepository {
    fun obtenerEquipos(): List<Equipo>
    fun obtenerEquipo(id: Int): Equipo?
    fun obtenerSolicitudes(): List<SolicitudPrestamo>
    fun obtenerSolicitud(id: Int): SolicitudPrestamo?
    fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit>
    fun cancelarSolicitud(id: Int): Result<Unit>
    
    // Thomas Isaza HUs
    fun login(correo: String): Result<Usuario>
    fun actualizarPerfil(usuario: Usuario): Result<Unit>
    fun toggleFavorite(equipoId: Int): Result<Unit>
    fun obtenerUsuarioLogueado(): Usuario?

    // Admin HUs (HU-09 a HU-16)
    fun registrarEquipo(equipo: Equipo): Result<Unit>
    fun actualizarEstadoEquipo(id: Int, estado: EstadoEquipo): Result<Unit>
    fun aprobarSolicitud(id: Int): Result<Unit>
    fun rechazarSolicitud(id: Int, justificacion: String): Result<Unit>
    fun registrarDevolucion(id: Int, novedad: String?, gravedad: NivelGravedad?): Result<Unit>
    fun obtenerTrazabilidad(equipoId: Int): List<SolicitudPrestamo>
    fun obtenerEstadisticas(): ReporteEstadistico
}
