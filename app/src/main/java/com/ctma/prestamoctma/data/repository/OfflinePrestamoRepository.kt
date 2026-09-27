package com.ctma.prestamoctma.data.repository

import com.ctma.prestamoctma.data.local.dao.EvidenciaDao
import com.ctma.prestamoctma.data.local.dao.PrestamoDao
import com.ctma.prestamoctma.data.local.entities.asEntity
import com.ctma.prestamoctma.data.local.entities.asExternalModel
import com.ctma.prestamoctma.data.local.entities.EvidenciaEntity
import com.ctma.prestamoctma.data.local.entities.UploadStatus
import com.ctma.prestamoctma.data.remote.dto.ArticuloDto
import com.ctma.prestamoctma.data.remote.dto.PrestamoDto
import com.ctma.prestamoctma.data.remote.dto.asDto
import com.ctma.prestamoctma.data.remote.dto.asEntity
import com.ctma.prestamoctma.data.remote.supabase
import com.ctma.prestamoctma.model.Equipo
import com.ctma.prestamoctma.model.EstadoEquipo
import com.ctma.prestamoctma.model.EstadoSolicitud
import com.ctma.prestamoctma.model.GravedadDano
import com.ctma.prestamoctma.model.SolicitudPrestamo
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.net.Uri
import android.content.Context

class OfflinePrestamoRepository(
    private val context: Context,
    private val prestamoDao: PrestamoDao,
    private val evidenciaDao: EvidenciaDao,
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

    override fun getEvidencias(prestamoId: String): Flow<List<EvidenciaEntity>> =
        evidenciaDao.getEvidenciasByPrestamo(prestamoId)

    override fun getAllEvidencias(): Flow<List<EvidenciaEntity>> =
        evidenciaDao.getAllEvidencias()

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

        // 3. Sincronizar con Supabase PRIMERO (o manejar fallo local)
        // Para este flujo, intentaremos remoto y si falla, lanzamos error (requiere internet para solicitudes nuevas)
        val dto = solicitud.asEntity().asDto()
        supabase.postgrest.from("prestamos").upsert(dto)
        
        // Actualizar estado del equipo en remoto también
        val articuloActualizado = articulo.copy(estado = EstadoEquipo.RESERVADO)
        supabase.postgrest.from("articulos").upsert(articuloActualizado.asDto())

        // 4. Si lo remoto fue bien, actualizamos local
        prestamoDao.insertPrestamo(solicitud.asEntity())
        prestamoDao.updateArticulo(articuloActualizado)
    }

    override suspend fun cancelarSolicitud(id: String): Result<Unit> = runCatching {
        val prestamo = prestamoDao.getPrestamoById(id)
            ?: throw Exception("Solicitud no encontrada")

        if ((prestamo.estado != EstadoSolicitud.SOLICITADA) && (prestamo.estado != EstadoSolicitud.APROBADA)) {
            throw Exception("No se puede cancelar una solicitud en estado ${prestamo.estado}")
        }

        val prestamoCancelado = prestamo.copy(estado = EstadoSolicitud.CANCELADA)
        
        // 1. Remoto
        supabase.postgrest.from("prestamos").upsert(prestamoCancelado.asDto())

        // 2. Liberar equipo en remoto
        val articulo = prestamoDao.getArticuloById(prestamo.equipoId)
        if (articulo != null) {
            val articuloLibre = articulo.copy(estado = EstadoEquipo.DISPONIBLE)
            supabase.postgrest.from("articulos").upsert(articuloLibre.asDto())
            
            // Local
            prestamoDao.updateArticulo(articuloLibre)
        }

        // 3. Local
        prestamoDao.updatePrestamo(prestamoCancelado)
    }

    override suspend fun procesarDevolucion(
        solicitudId: String,
        novedadDetalle: String?,
        gravedad: GravedadDano
    ): Result<Unit> = runCatching {
        val prestamo = prestamoDao.getPrestamoById(solicitudId)
            ?: throw Exception("Solicitud no encontrada")

        val prestamoDevuelto = prestamo.copy(
            estado = EstadoSolicitud.DEVUELTA,
            novedadDetalle = novedadDetalle,
            gravedadDano = gravedad
        )

        // 1. Remoto
        supabase.postgrest.from("prestamos").upsert(prestamoDevuelto.asDto())

        // 2. Actualizar estado del equipo (si hay daño grave, dejar en mantenimiento)
        val articulo = prestamoDao.getArticuloById(prestamo.equipoId)
        if (articulo != null) {
            val nuevoEstado = if (gravedad == GravedadDano.GRAVE || gravedad == GravedadDano.MODERADO) {
                EstadoEquipo.EN_MANTENIMIENTO
            } else {
                EstadoEquipo.DISPONIBLE
            }
            val articuloActualizado = articulo.copy(estado = nuevoEstado)
            
            supabase.postgrest.from("articulos").upsert(articuloActualizado.asDto())
            
            // Local
            prestamoDao.updateArticulo(articuloActualizado)
        }
        
        // Local prestamo
        prestamoDao.updatePrestamo(prestamoDevuelto)
    }

    override suspend fun agregarEquipo(equipo: Equipo): Result<Unit> = runCatching {
        val dto = equipo.asEntity().asDto()
        supabase.postgrest.from("articulos").upsert(dto)
        
        prestamoDao.insertArticulos(listOf(equipo.asEntity()))
    }

    override suspend fun refresh(): Result<Unit> = try {
        // 1. Fetch from Supabase
        val articulosRemotos = supabase.postgrest.from("articulos").select().decodeList<ArticuloDto>()
        val prestamosRemotos = supabase.postgrest.from("prestamos").select {
            order("fecha_solicitud", Order.DESCENDING)
        }.decodeList<PrestamoDto>()

        // 2. Update Room (Transaction-like)
        prestamoDao.insertArticulos(articulosRemotos.map { it.asEntity() })
        
        // Para préstamos, insertamos uno a uno o por lote si el DAO lo soporta
        prestamosRemotos.forEach { dto ->
            prestamoDao.insertPrestamo(dto.asEntity())
        }
        
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun guardarEvidenciaLocal(evidencia: EvidenciaEntity): Result<Unit> = runCatching {
        evidenciaDao.insertEvidencia(evidencia)
    }

    override suspend fun subirEvidencia(id: String): Result<Unit> = try {
        val evidencia = evidenciaDao.getEvidenciaById(id) 
            ?: throw Exception("Evidencia no encontrada")
        
        evidenciaDao.updateStatus(id, UploadStatus.SUBIENDO)

        val uri = Uri.parse(evidencia.localUri)
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("No se pudo abrir el archivo")
        
        val bytes = inputStream.use { it.readBytes() }
        val fileName = "${evidencia.prestamoId}/${evidencia.id}.${evidencia.mimeType.split("/").last()}"

        val bucket = supabase.storage.from("evidencias")
        bucket.upload(fileName, bytes, upsert = true)

        val remoteUrl = bucket.publicUrl(fileName)
        evidenciaDao.updateEvidencia(evidencia.copy(
            status = UploadStatus.SINCRONIZADA,
            remoteUrl = remoteUrl
        ))

        Result.success(Unit)
    } catch (e: Exception) {
        e.printStackTrace() // Imprime el error exacto en el Logcat para depuración
        evidenciaDao.updateStatus(id, UploadStatus.FALLIDA)
        Result.failure(e)
    }
}
