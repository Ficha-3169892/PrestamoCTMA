package com.example.prestamolabctma.data.remote.dto

import com.example.prestamolabctma.model.EstadoSolicitud
import com.example.prestamolabctma.model.NivelGravedad
import com.example.prestamolabctma.model.SolicitudPrestamo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SolicitudDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("equipoid") val equipoId: Int,
    @SerialName("ambientedestino") val ambienteDestino: String,
    @SerialName("proposito") val proposito: String,
    @SerialName("duracionhoras") val duracionHoras: Int,
    @SerialName("estado") val estado: EstadoSolicitud,
    @SerialName("fechasolicitud") val fechaSolicitud: Long = System.currentTimeMillis(),
    @SerialName("justificacionrechazo") val justificacionRechazo: String? = null,
    @SerialName("novedaddevolucion") val novedadDevolucion: String? = null,
    @SerialName("gravedadnovedad") val gravedadNovedad: NivelGravedad? = null
)

fun SolicitudDto.toDomain(): SolicitudPrestamo {
    return SolicitudPrestamo(
        id = id ?: 0,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = estado,
        fechaSolicitud = fechaSolicitud,
        justificacionRechazo = justificacionRechazo,
        novedadDevolucion = novedadDevolucion,
        gravedadNovedad = gravedadNovedad
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
        fechaSolicitud = fechaSolicitud,
        justificacionRechazo = justificacionRechazo,
        novedadDevolucion = novedadDevolucion,
        gravedadNovedad = gravedadNovedad
    )
}
