package com.example.prestamolabctma.model

@Suppress("unused")
enum class CategoriaEquipo {
    COMPUTO,
    HERRAMIENTAS,
    AUDIOVISUAL,
    OTROS
}

@Suppress("unused")
enum class EstadoEquipo {
    DISPONIBLE,
    RESERVADO,
    PRESTADO
}

@Suppress("unused")
enum class EstadoSolicitud {
    SOLICITADA,
    APROBADA,
    ENTREGADA,
    DEVUELTA,
    CANCELADA,
    RECHAZADA
}
