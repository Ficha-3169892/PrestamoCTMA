package com.ctma.prestamoctma.model

enum class CategoriaEquipo {
    COMPUTACION, AUDIOVISUAL, HERRAMIENTAS, OTROS
}

enum class EstadoEquipo {
    DISPONIBLE, RESERVADO, PRESTADO, EN_MANTENIMIENTO
}

enum class EstadoSolicitud {
    SOLICITADA, APROBADA, ENTREGADA, DEVUELTA, CANCELADA, RECHAZADA
}

enum class GravedadDano {
    NINGUNA, LEVE, MODERADO, GRAVE
}
