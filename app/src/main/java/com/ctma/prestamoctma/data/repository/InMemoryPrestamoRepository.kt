package com.ctma.prestamoctma.data.repository

import com.ctma.prestamoctma.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import java.util.Date

class InMemoryPrestamoRepository : PrestamoRepository {
    private val _equipos = MutableStateFlow(
        listOf(
            Equipo("E001", "Portátil HP ProBook", CategoriaEquipo.COMPUTACION, EstadoEquipo.DISPONIBLE, "Intel i5, 8GB RAM"),
            Equipo("E002", "Cámara Sony Alpha", CategoriaEquipo.AUDIOVISUAL, EstadoEquipo.DISPONIBLE, "4K Mirrorless"),
            Equipo("E003", "Multímetro Digital", CategoriaEquipo.HERRAMIENTAS, EstadoEquipo.EN_MANTENIMIENTO, "Dañado - Pantalla rota"),
            Equipo("E004", "iPad Air", CategoriaEquipo.COMPUTACION, EstadoEquipo.RESERVADO, "Apple M1 chip"),
            Equipo("E005", "Trípode Manfrotto", CategoriaEquipo.AUDIOVISUAL, EstadoEquipo.PRESTADO, "Fibra de carbono")
        )
    )

    private val _solicitudes = MutableStateFlow<List<SolicitudPrestamo>>(
        listOf(
            // 1. Solicitud VENCIDA (hace 10 horas) -> Banner Rojo
            SolicitudPrestamo(
                id = "TEST-VENCIDA",
                equipoId = "E005",
                nombreEquipo = "Trípode Manfrotto",
                usuarioId = "USER_CTMA_01",
                ambienteDestino = "Laboratorio A",
                proposito = "Práctica de fotografía",
                duracionHoras = 1,
                fechaSolicitud = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, -10) }.time,
                estado = EstadoSolicitud.SOLICITADA
            ),
            // 2. Solicitud POR VENCER (vence en 5 minutos) -> Tarjeta Amarilla y Snackbar
            SolicitudPrestamo(
                id = "TEST-POR-VENCER",
                equipoId = "E001",
                nombreEquipo = "Portátil HP ProBook",
                usuarioId = "USER_CTMA_01",
                ambienteDestino = "Aula 202",
                proposito = "Exposición proyecto",
                duracionHoras = 1,
                fechaSolicitud = Calendar.getInstance().apply { 
                    add(Calendar.MINUTE, -55) // Solicitado hace 55 min, duración 1h = vence en 5 min
                }.time,
                estado = EstadoSolicitud.SOLICITADA
            )
        )
    )

    override fun getEquipos(): Flow<List<Equipo>> = _equipos.asStateFlow()
    override fun getSolicitudes(): Flow<List<SolicitudPrestamo>> = _solicitudes.asStateFlow()
    override fun getEquipoById(id: String): Equipo? = _equipos.value.find { it.id == id }

    override suspend fun registrarSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        val equipo = getEquipoById(solicitud.equipoId)
        return if (equipo != null && equipo.estado == EstadoEquipo.DISPONIBLE) {
            _solicitudes.update { it + solicitud }
            actualizarEstadoEquipo(solicitud.equipoId, EstadoEquipo.RESERVADO)
            Result.success(Unit)
        } else Result.failure(Exception("No disponible"))
    }

    override suspend fun cancelarSolicitud(id: String): Result<Unit> {
        val solicitud = _solicitudes.value.find { it.id == id }
        return if (solicitud != null && solicitud.estado == EstadoSolicitud.SOLICITADA) {
            _solicitudes.update { list ->
                list.map { if (it.id == id) it.copy(estado = EstadoSolicitud.CANCELADA) else it }
            }
            actualizarEstadoEquipo(solicitud.equipoId, EstadoEquipo.DISPONIBLE)
            Result.success(Unit)
        } else Result.failure(Exception("No cancelable"))
    }

    override suspend fun procesarDevolucion(solicitudId: String, novedadDetalle: String?, gravedad: GravedadDano): Result<Unit> {
        val solicitud = _solicitudes.value.find { it.id == solicitudId }
        return if (solicitud != null) {
            _solicitudes.update { list ->
                list.map { if (it.id == solicitudId) it.copy(estado = EstadoSolicitud.DEVUELTA, novedadDetalle = novedadDetalle, gravedadDano = gravedad) else it }
            }
            val nuevoEstado = if (gravedad == GravedadDano.MODERADO || gravedad == GravedadDano.GRAVE) EstadoEquipo.EN_MANTENIMIENTO else EstadoEquipo.DISPONIBLE
            actualizarEstadoEquipo(solicitud.equipoId, nuevoEstado)
            Result.success(Unit)
        } else Result.failure(Exception("No encontrado"))
    }

    private fun actualizarEstadoEquipo(equipoId: String, nuevoEstado: EstadoEquipo) {
        _equipos.update { list -> list.map { if (it.id == equipoId) it.copy(estado = nuevoEstado) else it } }
    }
}
