package com.ctma.prestamolab.model

import kotlinx.serialization.Serializable

@Serializable
enum class CategoriaEquipo {
    ELECTRONICA,
    REDES,
    MEDICION,
    AUDIOVISUAL,
    HERRAMIENTA,
    PERIFERICO,
}

@Serializable
enum class EstadoEquipo {
    DISPONIBLE,
    RESERVADO,
    PRESTADO,
    MANTENIMIENTO,
    FUERA_DE_SERVICIO,
}

@Suppress("unused")
@Serializable
enum class EstadoSolicitud {
    SOLICITADA,
    APROBADA,
    ENTREGADA,
    DEVUELTA,
    CANCELADA,
    RECHAZADA,
}
