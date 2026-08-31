package com.ctma.prestamolab.model

data class SolicitudPrestamo(
    val id: Int,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: EstadoSolicitud,
    val justificacionRechazo: String? = null,
    val novedades: String? = null,
    val fechaCreacion: Long = System.currentTimeMillis(),
)
