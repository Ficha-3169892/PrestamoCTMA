package com.example.prestamolabctma.data.remote

import com.example.prestamolabctma.data.remote.dto.EquipoDto
import com.example.prestamolabctma.data.remote.dto.SolicitudDto
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Fuente de datos remota que interactúa con Supabase para gestionar
 * equipos y solicitudes de préstamo.
 */
class RemotePrestamoDataSource {

    private val client by lazy { supabase }

    /**
     * Obtiene la lista de todos los equipos disponibles en la base de datos de Supabase.
     */
    suspend fun fetchEquipos(): Result<List<EquipoDto>> = withContext(Dispatchers.IO) {
        try {
            val equipos = client.postgrest.from("equipos")
                .select()
                .decodeList<EquipoDto>()
            Result.success(equipos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene la lista de solicitudes de préstamo.
     */
    suspend fun fetchSolicitudes(): Result<List<SolicitudDto>> = withContext(Dispatchers.IO) {
        try {
            val solicitudes = client.postgrest.from("solicitudes")
                .select {
                    order("fechasolicitud", Order.DESCENDING)
                }
                .decodeList<SolicitudDto>()
            Result.success(solicitudes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inserta o actualiza un equipo en Supabase.
     */
    suspend fun upsertEquipo(dto: EquipoDto): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("equipos").upsert(dto) {
                onConflict = "serie"
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inserta o actualiza una solicitud de préstamo en Supabase.
     */
    suspend fun upsertSolicitud(dto: SolicitudDto): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("solicitudes").upsert(dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
