package com.example.prestamolabctma.model

@Suppress("unused")
data class SolicitudPrestamo(
    val id: Int,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: EstadoSolicitud,
    val fechaSolicitud: Long = System.currentTimeMillis(),
    val justificacionRechazo: String? = null,
    val novedadDevolucion: String? = null,
    val gravedadNovedad: NivelGravedad? = null,
)
