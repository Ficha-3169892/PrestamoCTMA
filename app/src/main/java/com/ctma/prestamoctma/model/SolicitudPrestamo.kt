package com.ctma.prestamoctma.model

import java.util.Date
import java.util.Calendar

data class SolicitudPrestamo(
    val id: String,
    val equipoId: String,
    val nombreEquipo: String,
    val usuarioId: String,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val fechaSolicitud: Date = Date(),
    val estado: EstadoSolicitud = EstadoSolicitud.SOLICITADA,
    val novedadDetalle: String? = null,
    val gravedadDano: GravedadDano = GravedadDano.NINGUNA
) {
    val fechaVencimiento: Date by lazy {
        Calendar.getInstance().apply {
            time = fechaSolicitud
            add(Calendar.HOUR_OF_DAY, duracionHoras)
        }.time
    }

    fun estaVencida(): Boolean {
        return Date().after(fechaVencimiento) && estado != EstadoSolicitud.DEVUELTA && estado != EstadoSolicitud.CANCELADA
    }

    fun faltaPocoParaVencer(): Boolean {
        val ahora = System.currentTimeMillis()
        val quinceMinutosEnMillis = 15 * 60 * 1000
        val tiempoRestante = fechaVencimiento.time - ahora
        return tiempoRestante in 1..quinceMinutosEnMillis && estado != EstadoSolicitud.DEVUELTA && estado != EstadoSolicitud.CANCELADA
    }
}
