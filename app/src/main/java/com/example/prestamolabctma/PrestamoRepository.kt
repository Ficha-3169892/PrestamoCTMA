package com.example.prestamolabctma

interface PrestamoRepository {
    fun obtenerEquipos(): List<Equipo>
    fun obtenerEquipo(id: Int): Equipo?
    fun obtenerSolicitudes(): List<SolicitudPrestamo>
    fun obtenerSolicitud(id: Int): SolicitudPrestamo?
    fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit>
    fun cancelarSolicitud(id: Int): Result<Unit>
}

class InMemoryPrestamoRepository : PrestamoRepository {
    private val equipos = mutableListOf(
        Equipo(1, "Laptop Dell Latitude", CategoriaEquipo.COMPUTO, EstadoEquipo.DISPONIBLE),
        Equipo(2, "Proyector Epson", CategoriaEquipo.AUDIOVISUAL, EstadoEquipo.DISPONIBLE),
        Equipo(3, "Kit de Herramientas Red", CategoriaEquipo.HERRAMIENTA, EstadoEquipo.PRESTADO),
        Equipo(4, "Osciloscopio Digital", CategoriaEquipo.ELECTRONICA, EstadoEquipo.RESERVADO),
        Equipo(5, "Tablet Samsung Galaxy Tab", CategoriaEquipo.COMPUTO, EstadoEquipo.DISPONIBLE)
    )

    private val solicitudes = mutableListOf<SolicitudPrestamo>()

    override fun obtenerEquipos(): List<Equipo> = equipos.toList()

    override fun obtenerEquipo(id: Int): Equipo? = equipos.find { it.id == id }

    override fun obtenerSolicitudes(): List<SolicitudPrestamo> = solicitudes.toList()

    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? = solicitudes.find { it.id == id }

    override fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        val equipoIndex = equipos.indexOfFirst { it.id == solicitud.equipoId }
        
        if (equipoIndex == -1) return Result.failure(Exception("Equipo no encontrado"))
        
        val equipo = equipos[equipoIndex]
        if (equipo.estado != EstadoEquipo.DISPONIBLE) {
            return Result.failure(Exception("El equipo no está disponible para préstamo"))
        }

        // Regla de negocio: Cambiar estado del equipo a RESERVADO
        equipos[equipoIndex] = equipo.copy(estado = EstadoEquipo.RESERVADO)
        solicitudes.add(solicitud)
        
        return Result.success(Unit)
    }

    override fun cancelarSolicitud(id: Int): Result<Unit> {
        val index = solicitudes.indexOfFirst { it.id == id }
        if (index == -1) return Result.failure(Exception("Solicitud no encontrada"))
        
        val solicitud = solicitudes[index]
        
        // Regla de negocio: Cancelar solo si está SOLICITADA (asumido por el corte del texto)
        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return Result.failure(Exception("Solo se pueden cancelar solicitudes en estado SOLICITADA"))
        }

        // Al cancelar, devolvemos el equipo a DISPONIBLE
        val equipoIndex = equipos.indexOfFirst { it.id == solicitud.equipoId }
        if (equipoIndex != -1) {
            equipos[equipoIndex] = equipos[equipoIndex].copy(estado = EstadoEquipo.DISPONIBLE)
        }

        solicitudes[index] = solicitud.copy(estado = EstadoSolicitud.CANCELADA)
        return Result.success(Unit)
    }
}
