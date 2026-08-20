package com.example.prestamolabctma.data.repository

import com.example.prestamolabctma.model.CategoriaEquipo
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.EstadoEquipo
import com.example.prestamolabctma.model.EstadoSolicitud
import com.example.prestamolabctma.model.SolicitudPrestamo

class InMemoryPrestamoRepository : PrestamoRepository {

    private val equipos = mutableListOf(
        Equipo(1, "Portátil Dell Latitude", CategoriaEquipo.COMPUTO, EstadoEquipo.DISPONIBLE),
        Equipo(2, "Videobeam Epson X41", CategoriaEquipo.AUDIOVISUAL, EstadoEquipo.DISPONIBLE),
        Equipo(3, "Kit Herramientas Pro", CategoriaEquipo.HERRAMIENTAS, EstadoEquipo.DISPONIBLE),
        Equipo(4, "Cámara Sony Alpha", CategoriaEquipo.AUDIOVISUAL, EstadoEquipo.RESERVADO),
        Equipo(5, "Tablet Samsung S7", CategoriaEquipo.COMPUTO, EstadoEquipo.PRESTADO)
    )

    private val solicitudes = mutableListOf<SolicitudPrestamo>()

    override fun obtenerEquipos(): List<Equipo> = equipos.toList()

    override fun obtenerEquipo(id: Int): Equipo? = equipos.find { it.id == id }

    override fun obtenerSolicitudes(): List<SolicitudPrestamo> = solicitudes.toList()

    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? = solicitudes.find { it.id == id }

    override fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        val equipo = obtenerEquipo(solicitud.equipoId)
            ?: return Result.failure(Exception("Equipo no encontrado"))

        // RN-01: Solo puede solicitarse un equipo DISPONIBLE
        if (equipo.estado != EstadoEquipo.DISPONIBLE) {
            return Result.failure(Exception("El equipo no está disponible para préstamo"))
        }

        // RN-06: Una solicitud activa debe modificar la disponibilidad del equipo
        val index = equipos.indexOf(equipo)
        equipos[index] = equipo.copy(estado = EstadoEquipo.RESERVADO)
        
        solicitudes.add(solicitud)
        return Result.success(Unit)
    }

    override fun cancelarSolicitud(id: Int): Result<Unit> {
        val solicitud = obtenerSolicitud(id)
            ?: return Result.failure(Exception("Solicitud no encontrada"))

        // RN-07: Solo una solicitud SOLICITADA puede cancelarse
        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return Result.failure(Exception("Solo se pueden cancelar solicitudes en estado SOLICITADA"))
        }

        // Al cancelar, devolvemos el equipo a DISPONIBLE si estaba reservado por esta solicitud
        val equipo = obtenerEquipo(solicitud.equipoId)
        if (equipo != null && equipo.estado == EstadoEquipo.RESERVADO) {
            val index = equipos.indexOf(equipo)
            equipos[index] = equipo.copy(estado = EstadoEquipo.DISPONIBLE)
        }

        val indexSolicitud = solicitudes.indexOf(solicitud)
        solicitudes[indexSolicitud] = solicitud.copy(estado = EstadoSolicitud.CANCELADA)

        return Result.success(Unit)
    }
}
