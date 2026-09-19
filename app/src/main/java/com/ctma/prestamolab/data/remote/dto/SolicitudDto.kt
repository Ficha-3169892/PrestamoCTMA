package com.ctma.prestamolab.data.remote.dto

import com.ctma.prestamolab.model.EstadoSolicitud
import com.ctma.prestamolab.model.SolicitudPrestamo
import kotlinx.serialization.Serializable

/**
 * DTO para la entidad Solicitud en Supabase.
 */
@Serializable
data class SolicitudDto(
    val id: Int? = null,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: EstadoSolicitud,
    val justificacionRechazo: String? = null,
    val novedades: String? = null,
    val fechaCreacion: Long
)

fun SolicitudDto.toDomain(): SolicitudPrestamo {
    return SolicitudPrestamo(
        id = id ?: 0,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = estado,
        justificacionRechazo = justificacionRechazo,
        novedades = novedades,
        fechaCreacion = fechaCreacion
    )
}

fun SolicitudPrestamo.toDto(): SolicitudDto {
    return SolicitudDto(
        id = if (id == 0) null else id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = estado,
        justificacionRechazo = justificacionRechazo,
        novedades = novedades,
        fechaCreacion = fechaCreacion
    )
}
