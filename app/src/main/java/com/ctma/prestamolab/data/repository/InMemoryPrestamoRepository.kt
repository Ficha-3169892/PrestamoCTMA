package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.model.*
import kotlinx.coroutines.flow.*

/**
 * [HU General] Implementación volátil para desarrollo rápido (Legacy).
 */
class InMemoryPrestamoRepository : PrestamoRepository {
    private val _equipos = MutableStateFlow<List<Equipo>>(listOf(
        Equipo(
            id = 1,
            nombre = "Kit de electrónica básica",
            serie = "EL-001-2023",
            marca = "Breadboard Tools",
            categoria = CategoriaEquipo.ELECTRONICA,
            estado = EstadoEquipo.DISPONIBLE,
            especificaciones = "Kit con protoboard, jumpers y set de resistencias.",
            accesorios = listOf("Pinza", "Estuche"),
            ubicacion = "Armario A - Cajón 1"
        )
    ))
    
    private val _solicitudes = MutableStateFlow<List<SolicitudPrestamo>>(emptyList())

    override fun observarEquipos(query: String): Flow<List<Equipo>> = _equipos.asStateFlow().map { lista ->
        if (query.isBlank()) lista
        else lista.filter { it.nombre.contains(query, ignoreCase = true) || it.serie.contains(query, ignoreCase = true) }
    }
    override fun observarSolicitudes(): Flow<List<SolicitudPrestamo>> = _solicitudes.asStateFlow()
    override fun observarTrazabilidad(equipoId: Int): Flow<List<SolicitudPrestamo>> = MutableStateFlow(emptyList())

    override suspend fun obtenerEquipo(id: Int): Equipo? = _equipos.value.find { it.id == id }
    override suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo? = _solicitudes.value.find { it.id == id }

    override suspend fun crearSolicitud(equipoId: Int, ambienteDestino: String, proposito: String, duracionHoras: Int): Result<Long> {
        val equipo = _equipos.value.find { it.id == equipoId } ?: return Result.failure(Exception("Equipo no encontrado"))
        if (equipo.estado != EstadoEquipo.DISPONIBLE) return Result.failure(Exception("Equipo no disponible"))

        val id = System.currentTimeMillis()
        val solicitud = SolicitudPrestamo(id.toInt(), equipoId, ambienteDestino, proposito, duracionHoras, EstadoSolicitud.SOLICITADA)
        _solicitudes.update { it + solicitud }
        
        // Simular reserva
        _equipos.update { lista ->
            lista.map { if (it.id == equipoId) it.copy(estado = EstadoEquipo.RESERVADO) else it }
        }
        
        return Result.success(id)
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> {
        val solicitud = _solicitudes.value.find { it.id == id } ?: return Result.failure(Exception("No existe"))
        _solicitudes.update { lista ->
            lista.map { if (it.id == id) it.copy(estado = EstadoSolicitud.CANCELADA) else it }
        }
        
        // Liberar equipo
        _equipos.update { lista ->
            lista.map { if (it.id == solicitud.equipoId) it.copy(estado = EstadoEquipo.DISPONIBLE) else it }
        }
        
        return Result.success(Unit)
    }
    override suspend fun conmutarFavorito(equipoId: Int): Result<Unit> = Result.success(Unit)
    override suspend fun agregarEquipo(equipo: Equipo): Result<Unit> = Result.success(Unit)
    override suspend fun aprobarSolicitud(id: Int): Result<Unit> = Result.success(Unit)
    override suspend fun rechazarSolicitud(id: Int, justificacion: String): Result<Unit> = Result.success(Unit)
    override suspend fun devolverEquipo(solicitudId: Int, novedades: String?): Result<Unit> = Result.success(Unit)
    override suspend fun obtenerEstadisticas(): Map<String, Int> = emptyMap()
}
