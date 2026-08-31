package com.example.prestamolabctma.model

@Suppress("unused")
enum class CategoriaEquipo {
    ELECTRONICA,
    REDES,
    HERRAMIENTAS,
    MEDICION,
    COMPUTO,
    AUDIOVISUAL,
    OTROS
}

@Suppress("unused")
enum class EstadoEquipo {
    DISPONIBLE,
    RESERVADO,
    PRESTADO,
    MANTENIMIENTO,
    FUERA_DE_SERVICIO
}

@Suppress("unused")
enum class EstadoSolicitud {
    SOLICITADA,
    APROBADA,
    ENTREGADA,
    DEVUELTA,
    CANCELADA,
    RECHAZADA,
    COMPLETADA
}

@Suppress("unused")
enum class NivelGravedad {
    LEVE,
    MODERADO,
    GRAVE
}
