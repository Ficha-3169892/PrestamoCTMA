package com.ctma.prestamoctma.data.repository

import com.ctma.prestamoctma.model.Equipo
import com.ctma.prestamoctma.model.GravedadDano
import com.ctma.prestamoctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow

interface PrestamoRepository {
    fun getEquipos(): Flow<List<Equipo>>
    fun getSolicitudes(): Flow<List<SolicitudPrestamo>>
    fun getEquipoById(id: String): Equipo?
    suspend fun registrarSolicitud(solicitud: SolicitudPrestamo): Result<Unit>
    suspend fun cancelarSolicitud(id: String): Result<Unit>
    suspend fun procesarDevolucion(
        solicitudId: String,
        novedadDetalle: String?,
        gravedad: GravedadDano
    ): Result<Unit>
}
