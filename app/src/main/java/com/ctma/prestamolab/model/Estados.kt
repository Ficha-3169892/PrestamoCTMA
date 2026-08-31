package com.ctma.prestamolab.model

enum class CategoriaEquipo {
    ELECTRONICA,
    REDES,
    MEDICION,
    AUDIOVISUAL,
    HERRAMIENTA,
    PERIFERICO,
}

enum class EstadoEquipo {
    DISPONIBLE,
    RESERVADO,
    PRESTADO,
    MANTENIMIENTO,
    FUERA_DE_SERVICIO,
}

@Suppress("unused")
enum class EstadoSolicitud {
    SOLICITADA,
    APROBADA,
    ENTREGADA,
    DEVUELTA,
    CANCELADA,
    RECHAZADA,
}
