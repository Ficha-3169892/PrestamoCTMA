package com.ctma.prestamolab.data.remote.dto

import com.ctma.prestamolab.model.EstadoSolicitud
import com.ctma.prestamolab.model.SolicitudPrestamo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para la entidad Solicitud en Supabase.
 * Usa @SerialName para coincidir exactamente con los nombres de columna en Postgres (minúsculas).
 */
@Serializable
data class SolicitudDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("equipoid") val equipoId: Int,
    @SerialName("usuarioid") val usuarioId: Int,
    @SerialName("ambientedestino") val ambienteDestino: String,
    @SerialName("proposito") val proposito: String,
    @SerialName("duracionhoras") val duracionHoras: Int,
    @SerialName("estado") val estado: EstadoSolicitud,
    @SerialName("justificacionrechazo") val justificacionRechazo: String? = null,
    @SerialName("novedades") val novedades: String? = null,
    @SerialName("fechacreacion") val fechaCreacion: Long
)

fun SolicitudDto.toDomain(): SolicitudPrestamo {
    return SolicitudPrestamo(
        id = id ?: 0,
        equipoId = equipoId,
        usuarioId = usuarioId,
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
        usuarioId = usuarioId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = estado,
        justificacionRechazo = justificacionRechazo,
        novedades = novedades,
        fechaCreacion = fechaCreacion
    )
}
