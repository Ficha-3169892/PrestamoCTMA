package com.ctma.prestamoctma.util

import com.ctma.prestamoctma.model.EstadoEquipo
import com.ctma.prestamoctma.model.EstadoSolicitud

object Validaciones {

    // RN-01: Propósito entre 10 y 180 caracteres
    fun validarProposito(proposito: String): Boolean {
        return proposito.trim().length in 10..180
    }

    // RN-02: Duración entre 1 y 8 horas
    fun validarDuracion(horas: Int): Boolean {
        return horas in 1..8
    }

    // RN-03: Obligatoriedad de ambiente/destino
    fun validarAmbienteDestino(ambiente: String): Boolean {
        return ambiente.trim().isNotEmpty()
    }

    // RN-04: Control de solicitudes sobre equipos no disponibles
    fun esEquipoDisponible(estado: EstadoEquipo): Boolean {
        return estado == EstadoEquipo.DISPONIBLE
    }

    // RN-06: Transición de estado permitida (Solo SOLICITADA a CANCELADA para el MVP)
    fun esTransicionPermitida(actual: EstadoSolicitud, nueva: EstadoSolicitud): Boolean {
        return if (actual == EstadoSolicitud.SOLICITADA && nueva == EstadoSolicitud.CANCELADA) {
            true
        } else {
            // Otras transiciones podrían definirse aquí según el flujo administrativo
            false
        }
    }
}
