package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.model.EstadoSolicitud
import com.ctma.prestamolab.model.SolicitudPrestamo

class InMemoryPrestamoRepository : PrestamoRepository {
    private val equipos = mutableListOf(
        Equipo(1, "Kit Arduino Uno R3", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
        Equipo(2, "Multímetro Digital Fluke", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
        Equipo(3, "Cámara Réflex Canon", CategoriaEquipo.AUDIOVISUAL, EstadoEquipo.RESERVADO)
    )
    private val solicitudes = mutableListOf<SolicitudPrestamo>()
    private var siguienteSolicitudId = 1

    override fun obtenerEquipos(): List<Equipo> = equipos.toList()
    override fun obtenerEquipo(id: Int): Equipo? = equipos.find { it.id == id }
    override fun obtenerSolicitudes(): List<SolicitudPrestamo> = solicitudes.toList()
    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? = solicitudes.find { it.id == id }

    override fun crearSolicitud(equipoId: Int, ambiente: String, proposito: String, duracion: Int): Result<SolicitudPrestamo> {
        val equipo = equipos.find { it.id == equipoId }
            ?: return Result.failure(IllegalArgumentException("El equipo especificado no existe"))

        if (equipo.estado != EstadoEquipo.DISPONIBLE) { // RN-01
            return Result.failure(IllegalStateException("El equipo no está disponible para préstamo"))
        }

        val nuevaSolicitud = SolicitudPrestamo(
            id = siguienteSolicitudId++,
            equipoId = equipoId,
            ambienteDestino = ambiente,
            proposito = proposito,
            duracionHoras = duracion,
            estado = EstadoSolicitud.SOLICITADA
        )
        solicitudes.add(nuevaSolicitud)

        // RN-06: Reservar automáticamente el equipo
        val index = equipos.indexOfFirst { it.id == equipoId }
        equipos[index] = equipo.copy(estado = EstadoEquipo.RESERVADO)

        return Result.success(nuevaSolicitud)
    }

    override fun cancelarSolicitud(id: Int): Result<Unit> {
        val solicitud = solicitudes.find { it.id == id }
            ?: return Result.failure(IllegalArgumentException("La solicitud no existe"))

        if (solicitud.estado != EstadoSolicitud.SOLICITADA) { // RN-07
            return Result.failure(IllegalStateException("Solo se pueden cancelar solicitudes en estado SOLICITADA"))
        }

        val indexSol = solicitudes.indexOfFirst { it.id == id }
        solicitudes[indexSol] = solicitud.copy(estado = EstadoSolicitud.CANCELADA)

        // RN-07: Liberar el equipo asignado haciéndolo DISPONIBLE de nuevo
        val indexEq = equipos.indexOfFirst { it.id == solicitud.equipoId }
        if (indexEq != -1) {
            equipos[indexEq] = equipos[indexEq].copy(estado = EstadoEquipo.DISPONIBLE)
        }

        return Result.success(Unit)
    }
}