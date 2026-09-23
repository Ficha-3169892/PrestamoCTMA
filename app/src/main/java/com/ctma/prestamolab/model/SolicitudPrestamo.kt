package com.ctma.prestamolab.model

import kotlinx.serialization.Serializable

@Serializable
data class SolicitudPrestamo(
    val id: Int,
    val equipoId: Int,
    val usuarioId: Int = 0, // [HU-03] Identificador del aprendiz
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: EstadoSolicitud,
    val justificacionRechazo: String? = null,
    val novedades: String? = null,
    val fechaCreacion: Long = System.currentTimeMillis(),
)
