package com.ctma.prestamolab.data.remote

import com.ctma.prestamolab.data.remote.dto.EquipoDto
import com.ctma.prestamolab.data.remote.dto.SolicitudDto
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Fuente de datos remota que interactúa con Supabase para gestionar
 * equipos y solicitudes de préstamo con alta tolerancia a errores.
 */
class RemotePrestamoDataSource {

    private val client = supabase

    /**
     * Obtiene la lista de todos los equipos disponibles en la base de datos (tabla 'equipos' o 'articulos').
     */
    suspend fun fetchEquipos(): Result<List<EquipoDto>> = withContext(Dispatchers.IO) {
        if (client.supabaseUrl.contains("YOUR_PROJECT_URL")) {
            return@withContext Result.success(emptyList())
        }
        try {
            val equipos = try {
                client.postgrest.from("equipos")
                    .select()
                    .decodeList<EquipoDto>()
            } catch (_: Exception) {
                client.postgrest.from("articulos")
                    .select()
                    .decodeList<EquipoDto>()
            }
            Result.success(equipos)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Obtiene la lista de solicitudes de préstamo, ordenadas por fecha de creación descendente.
     */
    suspend fun fetchSolicitudes(): Result<List<SolicitudDto>> = withContext(Dispatchers.IO) {
        if (client.supabaseUrl.contains("YOUR_PROJECT_URL")) {
            return@withContext Result.success(emptyList())
        }
        try {
            val solicitudes = try {
                client.postgrest.from("solicitudes")
                    .select {
                        order("fechacreacion", Order.DESCENDING)
                    }
                    .decodeList<SolicitudDto>()
            } catch (_: Exception) {
                client.postgrest.from("solicitudes")
                    .select()
                    .decodeList<SolicitudDto>()
            }
            Result.success(solicitudes)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Inserta o actualiza un equipo en la base de datos (tabla 'equipos' o 'articulos').
     */
    suspend fun upsertEquipo(dto: EquipoDto): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            try {
                if (!dto.serie.isNullOrBlank()) {
                    try {
                        client.postgrest.from("equipos").upsert(dto) {
                            onConflict = "serie"
                        }
                    } catch (_: Exception) {
                        client.postgrest.from("equipos").upsert(dto)
                    }
                } else {
                    client.postgrest.from("equipos").upsert(dto)
                }
            } catch (_: Exception) {
                client.postgrest.from("articulos").upsert(dto)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Inserta o actualiza una solicitud de préstamo en la base de datos.
     */
    suspend fun upsertSolicitud(dto: SolicitudDto): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("solicitudes").upsert(dto)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
