package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.model.EstadoSolicitud
import com.ctma.prestamolab.model.SolicitudPrestamo

class InMemoryPrestamoRepository : PrestamoRepository {
    private val equipos = mutableListOf(
        Equipo(
            id = 1,
            nombre = "Kit de electrónica básica",
            serie = "EL-001-2023",
            marca = "Breadboard Tools",
            categoria = CategoriaEquipo.ELECTRONICA,
            estado = EstadoEquipo.DISPONIBLE,
            especificaciones = "Kit con protoboard, jumpers y set de resistencias.",
            accesorios = listOf("Pinza", "Estuche"),
            ubicacion = "Armario A - Cajón 1",
        ),
        Equipo(
            id = 2,
            nombre = "Multímetro digital",
            serie = "MT-550-98",
            marca = "Fluke",
            categoria = CategoriaEquipo.MEDICION,
            estado = EstadoEquipo.DISPONIBLE,
            especificaciones = "Medición de voltaje, corriente y resistencia con precisión de 0.5%.",
            accesorios = listOf("Puntas de prueba", "Batería 9V instalada"),
            ubicacion = "Estante B - Mesa 2"
        ),
        Equipo(
            id = 3,
            nombre = "Tableta de pruebas",
            serie = "TP-992-K",
            marca = "TechTest",
            categoria = CategoriaEquipo.PERIFERICO,
            estado = EstadoEquipo.RESERVADO,
            especificaciones = "Pantalla táctil de 10 pulgadas para testing de apps.",
            accesorios = listOf("Cargador USB-C", "Lápiz óptico"),
            ubicacion = "Almacén Central - Rack 3"
        ),
        Equipo(
            id = 4,
            nombre = "Cámara de documentación",
            serie = "CAM-4K-02",
            marca = "Sony",
            categoria = CategoriaEquipo.AUDIOVISUAL,
            estado = EstadoEquipo.DISPONIBLE,
            especificaciones = "Resolución 4K, 60fps, ideal para grabar exposiciones.",
            accesorios = listOf("Trípode", "Memoria SD 64GB", "Batería extra"),
            ubicacion = "Laboratorio Multimedia - Gabinete 5"
        ),
        Equipo(
            id = 5,
            nombre = "Juego de herramientas manuales",
            serie = "HT-MAN-15",
            marca = "Stanley",
            categoria = CategoriaEquipo.HERRAMIENTA,
            estado = EstadoEquipo.PRESTADO,
            especificaciones = "Juego de 15 piezas con destornilladores, alicates y llaves.",
            accesorios = listOf("Caja transportable"),
            ubicacion = "Taller de Mantenimiento - Mueble Rojo"
        )
    )

    private val solicitudes = mutableListOf<SolicitudPrestamo>()
    private var siguienteSolicitudId = 1

    override fun obtenerEquipos(): List<Equipo> = equipos.toList()

    override fun obtenerEquipo(id: Int): Equipo? = equipos.firstOrNull { it.id == id }

    override fun obtenerSolicitudes(): List<SolicitudPrestamo> = solicitudes.toList()

    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? = solicitudes.firstOrNull { it.id == id }

    override fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Result<SolicitudPrestamo> {
        val equipo = obtenerEquipo(equipoId)
            ?: return Result.failure(IllegalArgumentException("El equipo solicitado no existe."))

        if (equipo.estado != EstadoEquipo.DISPONIBLE) {
            return Result.failure(IllegalStateException("Solo se puede solicitar un equipo disponible."))
        }

        val solicitud = SolicitudPrestamo(
            id = siguienteSolicitudId++,
            equipoId = equipoId,
            ambienteDestino = ambienteDestino.trim(),
            proposito = proposito.trim(),
            duracionHoras = duracionHoras,
            estado = EstadoSolicitud.SOLICITADA
        )

        solicitudes.add(solicitud)
        cambiarEstadoEquipo(equipoId, EstadoEquipo.RESERVADO)
        return Result.success(solicitud)
    }

    override fun cancelarSolicitud(id: Int): Result<Unit> {
        val index = solicitudes.indexOfFirst { it.id == id }
        if (index == -1) {
            return Result.failure(IllegalArgumentException("La solicitud no existe."))
        }

        val solicitud = solicitudes[index]
        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return Result.failure(IllegalStateException("Solo se puede cancelar una solicitud en estado SOLICITADA."))
        }

        solicitudes[index] = solicitud.copy(estado = EstadoSolicitud.CANCELADA)
        cambiarEstadoEquipo(solicitud.equipoId, EstadoEquipo.DISPONIBLE)
        return Result.success(Unit)
    }

    override fun conmutarFavorito(equipoId: Int): Result<Unit> {
        val index = equipos.indexOfFirst { it.id == equipoId }
        if (index >= 0) {
            equipos[index] = equipos[index].copy(esFavorito = !equipos[index].esFavorito)
            return Result.success(Unit)
        }
        return Result.failure(IllegalArgumentException("Equipo no encontrado"))
    }

    override fun agregarEquipo(equipo: Equipo): Result<Unit> {
        if (equipos.any { it.serie == equipo.serie }) {
            return Result.failure(IllegalArgumentException("El número de serie ya existe."))
        }
        val nuevoEquipo = equipo.copy(id = (equipos.maxOfOrNull { it.id } ?: 0) + 1)
        equipos.add(nuevoEquipo)
        return Result.success(Unit)
    }

    override fun aprobarSolicitud(id: Int): Result<Unit> {
        val index = solicitudes.indexOfFirst { it.id == id }
        if (index == -1) return Result.failure(IllegalArgumentException("Solicitud no encontrada"))
        
        val solicitud = solicitudes[index]
        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return Result.failure(IllegalStateException("La solicitud ya no está en estado SOLICITADA"))
        }

        solicitudes[index] = solicitud.copy(estado = EstadoSolicitud.APROBADA)
        cambiarEstadoEquipo(solicitud.equipoId, EstadoEquipo.PRESTADO)
        return Result.success(Unit)
    }

    override fun rechazarSolicitud(id: Int, justificacion: String): Result<Unit> {
        val index = solicitudes.indexOfFirst { it.id == id }
        if (index == -1) return Result.failure(IllegalArgumentException("Solicitud no encontrada"))
        
        val solicitud = solicitudes[index]
        solicitudes[index] = solicitud.copy(
            estado = EstadoSolicitud.RECHAZADA,
            justificacionRechazo = justificacion
        )
        cambiarEstadoEquipo(solicitud.equipoId, EstadoEquipo.DISPONIBLE)
        return Result.success(Unit)
    }

    override fun devolverEquipo(solicitudId: Int, novedades: String?): Result<Unit> {
        val index = solicitudes.indexOfFirst { it.id == solicitudId }
        if (index == -1) return Result.failure(IllegalArgumentException("Solicitud no encontrada"))
        
        val solicitud = solicitudes[index]
        solicitudes[index] = solicitud.copy(
            estado = EstadoSolicitud.DEVUELTA,
            novedades = novedades
        )
        
        val equipoIndex = equipos.indexOfFirst { it.id == solicitud.equipoId }
        if (equipoIndex != -1) {
            val nuevoEstado = if (!novedades.isNullOrBlank()) {
                EstadoEquipo.MANTENIMIENTO
            } else {
                EstadoEquipo.DISPONIBLE
            }
            equipos[equipoIndex] = equipos[equipoIndex].copy(estado = nuevoEstado)
        }
        
        return Result.success(Unit)
    }

    override fun obtenerEstadisticas(): Map<String, Int> {
        val stats = mutableMapOf<String, Int>()
        solicitudes.forEach { sol ->
            val equipo = obtenerEquipo(sol.equipoId)
            if (equipo != null) {
                stats[equipo.categoria.name] = (stats[equipo.categoria.name] ?: 0) + 1
                stats[equipo.nombre] = (stats[equipo.nombre] ?: 0) + 1
            }
        }
        return stats
    }

    override fun obtenerTrazabilidad(equipoId: Int): List<SolicitudPrestamo> {
        return solicitudes.asSequence()
            .filter { it.equipoId == equipoId }
            .sortedByDescending { it.fechaCreacion }
            .toList()
    }

    private fun cambiarEstadoEquipo(equipoId: Int, estado: EstadoEquipo) {
        val index = equipos.indexOfFirst { it.id == equipoId }
        if (index >= 0) {
            equipos[index] = equipos[index].copy(estado = estado)
        }
    }
}
