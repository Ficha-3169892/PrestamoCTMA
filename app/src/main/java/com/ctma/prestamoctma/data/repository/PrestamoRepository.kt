package com.ctma.prestamoctma.data.repository

import com.ctma.prestamoctma.model.Equipo
import com.ctma.prestamoctma.model.GravedadDano
import com.ctma.prestamoctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow

interface PrestamoRepository {
    fun getEquipos(): Flow<List<Equipo>>
    fun getSolicitudes(): Flow<List<SolicitudPrestamo>>
    fun getSolicitudesByUsuario(usuarioId: String): Flow<List<SolicitudPrestamo>>
    suspend fun getEquipoById(id: String): Equipo?
    suspend fun getSolicitudById(id: String): SolicitudPrestamo?
    suspend fun registrarSolicitud(solicitud: SolicitudPrestamo): Result<Unit>
    suspend fun cancelarSolicitud(id: String): Result<Unit>
    suspend fun procesarDevolucion(
        solicitudId: String,
        novedadDetalle: String?,
        gravedad: GravedadDano
    ): Result<Unit>
    suspend fun agregarEquipo(equipo: Equipo): Result<Unit>
    suspend fun refresh(): Result<Unit>
}
