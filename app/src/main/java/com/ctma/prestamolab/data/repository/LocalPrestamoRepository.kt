package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.data.local.dao.EquipoDao
import com.ctma.prestamolab.data.local.dao.SolicitudDao
import com.ctma.prestamolab.data.local.entity.EquipoEntity
import com.ctma.prestamolab.data.local.entity.SolicitudEntity
import com.ctma.prestamolab.data.remote.RemotePrestamoDataSource
import com.ctma.prestamolab.data.remote.dto.toDomain
import com.ctma.prestamolab.data.remote.dto.toDto
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.model.EstadoSolicitud
import com.ctma.prestamolab.model.SolicitudPrestamo
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [Semana 06] Implementación del repositorio usando Room como Fuente Única de Verdad (SSOT).
 * Modificado para soportar el patrón de persistencia híbrida con Supabase.
 */
class LocalPrestamoRepository(
    private val equipoDao: EquipoDao,
    private val solicitudDao: SolicitudDao,
    private val remoteDataSource: RemotePrestamoDataSource
) : PrestamoRepository {

    override fun observarEquipos(query: String): Flow<List<Equipo>> {
        val flow = if (query.isBlank()) equipoDao.obtenerTodos() else equipoDao.buscar(query)
        return flow.map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observarSolicitudes(): Flow<List<SolicitudPrestamo>> {
        return solicitudDao.obtenerTodas().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observarTrazabilidad(equipoId: Int): Flow<List<SolicitudPrestamo>> {
        return solicitudDao.obtenerPorEquipo(equipoId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun obtenerEquipo(id: Int): Equipo? {
        return equipoDao.obtenerPorId(id)?.toDomain()
    }

    override suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo? {
        return solicitudDao.obtenerPorId(id)?.toDomain()
    }

    override suspend fun sincronizar(): Result<Unit> = coroutineScope {
        try {
            val equiposDeferred = async { remoteDataSource.fetchEquipos() }
            val solicitudesDeferred = async { remoteDataSource.fetchSolicitudes() }
            
            val equiposResult = equiposDeferred.await()
            val solicitudesResult = solicitudesDeferred.await()
            
            val equiposDto = equiposResult.getOrThrow()
            val solicitudesDto = solicitudesResult.getOrThrow()
            
            equipoDao.insertarLista(equiposDto.map { it.toDomain().toEntity() })
            solicitudDao.insertarLista(solicitudesDto.map { it.toDomain().toEntity() })
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Result<Long> {
        val equipo = equipoDao.obtenerPorId(equipoId) ?: return Result.failure(Exception("Equipo no encontrado"))
        
        val solicitud = SolicitudEntity(
            equipoId = equipoId,
            ambienteDestino = ambienteDestino,
            proposito = proposito,
            duracionHoras = duracionHoras,
            estado = EstadoSolicitud.SOLICITADA
        )
        
        val id = solicitudDao.insertar(solicitud)
        
        // [RN-06] Al guardar con éxito, el equipo pasa automáticamente a RESERVADO
        val equipoReservado = equipo.copy(estado = EstadoEquipo.RESERVADO)
        equipoDao.actualizar(equipoReservado)
        
        try {
            remoteDataSource.upsertSolicitud(solicitud.copy(id = id.toInt()).toDomain().toDto())
            remoteDataSource.upsertEquipo(equipoReservado.toDomain().toDto())
        } catch (e: Exception) {
            // Manejar excepciones de red para evitar romper la experiencia local
        }
        
        return Result.success(id)
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> {
        val solicitud = solicitudDao.obtenerPorId(id) ?: return Result.failure(Exception("Solicitud no encontrada"))
        val solicitudCancelada = solicitud.copy(estado = EstadoSolicitud.CANCELADA)
        solicitudDao.actualizar(solicitudCancelada)
        
        // Liberar equipo
        val equipo = equipoDao.obtenerPorId(solicitud.equipoId)
        val equipoDisponible = equipo?.copy(estado = EstadoEquipo.DISPONIBLE)
        equipoDisponible?.let { equipoDao.actualizar(it) }
        
        try {
            remoteDataSource.upsertSolicitud(solicitudCancelada.toDomain().toDto())
            equipoDisponible?.let { remoteDataSource.upsertEquipo(it.toDomain().toDto()) }
        } catch (e: Exception) {
            // Manejar excepciones de red para evitar romper la experiencia local
        }
        
        return Result.success(Unit)
    }

    override suspend fun conmutarFavorito(equipoId: Int): Result<Unit> {
        equipoDao.conmutarFavorito(equipoId)
        return Result.success(Unit)
    }

    override suspend fun agregarEquipo(equipo: Equipo): Result<Unit> {
        equipoDao.insertar(equipo.toEntity())
        return Result.success(Unit)
    }

    override suspend fun aprobarSolicitud(id: Int): Result<Unit> {
        val solicitud = solicitudDao.obtenerPorId(id) ?: return Result.failure(Exception("Solicitud no encontrada"))
        val solicitudAprobada = solicitud.copy(estado = EstadoSolicitud.APROBADA)
        solicitudDao.actualizar(solicitudAprobada)
        
        val equipo = equipoDao.obtenerPorId(solicitud.equipoId)
        val equipoPrestado = equipo?.copy(estado = EstadoEquipo.PRESTADO)
        equipoPrestado?.let { equipoDao.actualizar(it) }
        
        try {
            remoteDataSource.upsertSolicitud(solicitudAprobada.toDomain().toDto())
            equipoPrestado?.let { remoteDataSource.upsertEquipo(it.toDomain().toDto()) }
        } catch (e: Exception) {
            // Manejar excepciones de red para evitar romper la experiencia local
        }
        
        return Result.success(Unit)
    }

    override suspend fun rechazarSolicitud(id: Int, justificacion: String): Result<Unit> {
        val solicitud = solicitudDao.obtenerPorId(id) ?: return Result.failure(Exception("Solicitud no encontrada"))
        val solicitudRechazada = solicitud.copy(estado = EstadoSolicitud.RECHAZADA, justificacionRechazo = justificacion)
        solicitudDao.actualizar(solicitudRechazada)
        
        val equipo = equipoDao.obtenerPorId(solicitud.equipoId)
        val equipoDisponible = equipo?.copy(estado = EstadoEquipo.DISPONIBLE)
        equipoDisponible?.let { equipoDao.actualizar(it) }
        
        try {
            remoteDataSource.upsertSolicitud(solicitudRechazada.toDomain().toDto())
            equipoDisponible?.let { remoteDataSource.upsertEquipo(it.toDomain().toDto()) }
        } catch (e: Exception) {
            // Manejar excepciones de red para evitar romper la experiencia local
        }
        
        return Result.success(Unit)
    }

    override suspend fun devolverEquipo(solicitudId: Int, novedades: String?): Result<Unit> {
        val solicitud = solicitudDao.obtenerPorId(solicitudId) ?: return Result.failure(Exception("Solicitud no encontrada"))
        val solicitudDevuelta = solicitud.copy(estado = EstadoSolicitud.DEVUELTA, novedades = novedades)
        solicitudDao.actualizar(solicitudDevuelta)
        
        val equipo = equipoDao.obtenerPorId(solicitud.equipoId)
        val nuevoEstado = if (novedades.isNullOrBlank()) EstadoEquipo.DISPONIBLE else EstadoEquipo.MANTENIMIENTO
        val equipoActualizado = equipo?.copy(estado = nuevoEstado)
        equipoActualizado?.let { equipoDao.actualizar(it) }
        
        try {
            remoteDataSource.upsertSolicitud(solicitudDevuelta.toDomain().toDto())
            equipoActualizado?.let { remoteDataSource.upsertEquipo(it.toDomain().toDto()) }
        } catch (e: Exception) {
            // Manejar excepciones de red para evitar romper la experiencia local
        }
        
        return Result.success(Unit)
    }

    override suspend fun obtenerEstadisticas(): Map<String, Int> {
        return emptyMap()
    }

    // Mappers
    private fun EquipoEntity.toDomain() = Equipo(
        id = id,
        nombre = nombre,
        serie = serie,
        marca = marca,
        categoria = categoria,
        estado = estado,
        especificaciones = especificaciones,
        accesorios = accesorios.split(",").filter { it.isNotBlank() },
        ubicacion = ubicacion,
        imagenUrl = imagenUrl,
        esFavorito = esFavorito
    )

    private fun SolicitudEntity.toDomain() = SolicitudPrestamo(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = estado,
        fechaCreacion = fechaCreacion,
        justificacionRechazo = justificacionRechazo,
        novedades = novedades
    )
    
    private fun Equipo.toEntity() = EquipoEntity(
        id = id,
        nombre = nombre,
        serie = serie,
        marca = marca,
        categoria = categoria,
        estado = estado,
        especificaciones = especificaciones,
        accesorios = accesorios.joinToString(","),
        ubicacion = ubicacion,
        imagenUrl = imagenUrl,
        esFavorito = esFavorito
    )

    private fun SolicitudPrestamo.toEntity() = SolicitudEntity(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = estado,
        fechaCreacion = fechaCreacion,
        justificacionRechazo = justificacionRechazo,
        novedades = novedades
    )
}
