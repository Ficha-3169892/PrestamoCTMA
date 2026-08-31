package com.ctma.prestamolab.domain

fun ambienteValido(texto: String): Boolean = texto.trim().isNotEmpty()

fun propositoValido(texto: String): Boolean = texto.trim().length in (10..180)

fun duracionValida(horas: Int): Boolean = horas in (1..8)

data class ErroresSolicitud(
    val ambiente: String? = null,
    val proposito: String? = null,
    val duracion: String? = null,
) {
    val hayErrores: Boolean
        get() = (ambiente != null) || (proposito != null) || (duracion != null)
}

fun validarSolicitud(
    ambienteDestino: String,
    proposito: String,
    duracionHoras: Int?,
): ErroresSolicitud {
    return ErroresSolicitud(
        ambiente = if (ambienteValido(ambienteDestino)) null else "El ambiente o destino es obligatorio.",
        proposito = if (propositoValido(proposito)) null else "El propósito debe tener entre 10 y 180 caracteres.",
        duracion = if ((duracionHoras != null) && duracionValida(duracionHoras)) null else "La duración debe estar entre 1 y 8 horas.",
    )
}
