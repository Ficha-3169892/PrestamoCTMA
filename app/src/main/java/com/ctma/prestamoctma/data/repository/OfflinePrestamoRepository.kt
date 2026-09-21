package com.ctma.prestamoctma.data.repository

import com.ctma.prestamoctma.data.local.dao.PrestamoDao
import com.ctma.prestamoctma.data.local.entities.asEntity
import com.ctma.prestamoctma.data.local.entities.asExternalModel
import com.ctma.prestamoctma.model.Equipo
import com.ctma.prestamoctma.model.EstadoEquipo
import com.ctma.prestamoctma.model.EstadoSolicitud
import com.ctma.prestamoctma.model.GravedadDano
import com.ctma.prestamoctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflinePrestamoRepository(
    private val prestamoDao: PrestamoDao,
) : PrestamoRepository {

    override fun getEquipos(): Flow<List<Equipo>> =
        prestamoDao.getAllArticulos().map { entities ->
            entities.map { it.asExternalModel() }
        }

    override fun getSolicitudes(): Flow<List<SolicitudPrestamo>> =
        prestamoDao.getAllPrestamos().map { entities ->
            entities.map { it.asExternalModel() }
        }

    override fun getSolicitudesByUsuario(usuarioId: String): Flow<List<SolicitudPrestamo>> =
        prestamoDao.getPrestamosByUsuario(usuarioId).map { entities ->
            entities.map { it.asExternalModel() }
        }

    override suspend fun getEquipoById(id: String): Equipo? =
        prestamoDao.getArticuloById(id)?.asExternalModel()

    override suspend fun getSolicitudById(id: String): SolicitudPrestamo? =
        prestamoDao.getPrestamoById(id)?.asExternalModel()

    override suspend fun registrarSolicitud(solicitud: SolicitudPrestamo): Result<Unit> = runCatching {
        // 1. Obtener el equipo
        val articulo = prestamoDao.getArticuloById(solicitud.equipoId)
            ?: throw Exception("Equipo no encontrado")

        // 2. Verificar disponibilidad
        if (articulo.estado != EstadoEquipo.DISPONIBLE) {
            throw Exception("El equipo no está disponible")
        }

        // 3. Insertar solicitud
        prestamoDao.insertPrestamo(solicitud.asEntity())

        // 4. Actualizar estado del equipo
        prestamoDao.updateArticulo(articulo.copy(estado = EstadoEquipo.RESERVADO))
    }

    override suspend fun cancelarSolicitud(id: String): Result<Unit> = runCatching {
        val prestamo = prestamoDao.getPrestamoById(id)
            ?: throw Exception("Solicitud no encontrada")

        if ((prestamo.estado != EstadoSolicitud.SOLICITADA) && (prestamo.estado != EstadoSolicitud.APROBADA)) {
            throw Exception("No se puede cancelar una solicitud en estado ${prestamo.estado}")
        }

        // 1. Actualizar solicitud
        prestamoDao.updatePrestamo(prestamo.copy(estado = EstadoSolicitud.CANCELADA))

        // 2. Liberar equipo
        prestamoDao.getArticuloById(prestamo.equipoId)?.let { articulo ->
            prestamoDao.updateArticulo(articulo.copy(estado = EstadoEquipo.DISPONIBLE))
        }
    }

    override suspend fun procesarDevolucion(
        solicitudId: String,
        novedadDetalle: String?,
        gravedad: GravedadDano
    ): Result<Unit> = runCatching {
        val prestamo = prestamoDao.getPrestamoById(solicitudId)
            ?: throw Exception("Solicitud no encontrada")

        // 1. Actualizar solicitud
        prestamoDao.updatePrestamo(
            prestamo.copy(
                estado = EstadoSolicitud.DEVUELTA,
                novedadDetalle = novedadDetalle,
                gravedadDano = gravedad
            )
        )

        // 2. Actualizar estado del equipo (si hay daño grave, dejar en mantenimiento)
        val articulo = prestamoDao.getArticuloById(prestamo.equipoId)
        if (articulo != null) {
            val nuevoEstado = if (gravedad == GravedadDano.GRAVE || gravedad == GravedadDano.MODERADO) {
                EstadoEquipo.EN_MANTENIMIENTO
            } else {
                EstadoEquipo.DISPONIBLE
            }
            prestamoDao.updateArticulo(articulo.copy(estado = nuevoEstado))
        }
    }

    override suspend fun agregarEquipo(equipo: Equipo): Result<Unit> = runCatching {
        prestamoDao.insertArticulos(listOf(equipo.asEntity()))
    }
}
