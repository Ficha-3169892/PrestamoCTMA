package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow

interface PrestamoRepository {
    // [HU General] Observables para el estado reactivo (Semana 07)
    fun observarEquipos(query: String = ""): Flow<List<Equipo>>
    fun observarSolicitudes(): Flow<List<SolicitudPrestamo>>
    fun observarTrazabilidad(equipoId: Int): Flow<List<SolicitudPrestamo>>
    
    suspend fun obtenerEquipo(id: Int): Equipo?
    suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo?
    
    suspend fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Result<Long>

    suspend fun cancelarSolicitud(id: Int): Result<Unit>
    suspend fun conmutarFavorito(equipoId: Int): Result<Unit>
    
    suspend fun agregarEquipo(equipo: Equipo): Result<Unit>
    suspend fun aprobarSolicitud(id: Int): Result<Unit>
    suspend fun rechazarSolicitud(id: Int, justificacion: String): Result<Unit>
    suspend fun devolverEquipo(solicitudId: Int, novedades: String?): Result<Unit>
    
    suspend fun sincronizar(): Result<Unit> = Result.success(Unit)
    
    suspend fun obtenerEstadisticas(): Map<String, Int>
    
    // Compatibilidad temporal
    fun obtenerEquipos(): List<Equipo> = emptyList()
    fun obtenerSolicitudes(): List<SolicitudPrestamo> = emptyList()
    fun obtenerTrazabilidad(equipoId: Int): List<SolicitudPrestamo> = emptyList()
}
