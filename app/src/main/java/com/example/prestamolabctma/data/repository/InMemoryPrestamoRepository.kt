package com.example.prestamolabctma.data.repository

import com.example.prestamolabctma.model.*

class InMemoryPrestamoRepository : PrestamoRepository {

    private val equipos = mutableListOf(
        Equipo(
            id = 1,
            nombre = "Portátil Dell Latitude",
            categoria = CategoriaEquipo.COMPUTO,
            estado = EstadoEquipo.DISPONIBLE,
            numSerie = "DELL-LAT-001",
            marca = "Dell",
            especificaciones = "Intel Core i7, 16GB RAM, 512GB SSD. Windows 11 Pro.",
            accesorios = "Cargador original, Maletín protector.",
            ubicacion = "Laboratorio de Cómputo - Estante A1",
            imageUrl = "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800"
        ),
        Equipo(
            id = 2,
            nombre = "Router Cisco 2901",
            categoria = CategoriaEquipo.REDES,
            estado = EstadoEquipo.DISPONIBLE,
            numSerie = "CIS-2901-44",
            marca = "Cisco Systems",
            especificaciones = "Router de servicios integrados con 2 puertos GE, 4 slots HWIC.",
            accesorios = "Cable de poder, Cable de consola.",
            ubicacion = "Laboratorio de Redes - Gabinete Central",
            imageUrl = "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=800"
        ),
        Equipo(
            id = 3,
            nombre = "Kit Herramientas Pro",
            categoria = CategoriaEquipo.HERRAMIENTAS,
            estado = EstadoEquipo.DISPONIBLE,
            numSerie = "KIT-HER-789",
            marca = "Stanley / Tramontina",
            especificaciones = "Juego de 50 piezas para mantenimiento electrónico y mecánico.",
            accesorios = "Estuche rígido, Juego de destornilladores, Multímetro básico.",
            ubicacion = "Taller de Electrónica - Armario Herramientas",
            imageUrl = "https://images.unsplash.com/photo-1581244276894-067d73010b95?w=800"
        ),
        Equipo(
            id = 4,
            nombre = "Multímetro Digital Fluke",
            categoria = CategoriaEquipo.MEDICION,
            estado = EstadoEquipo.DISPONIBLE,
            numSerie = "FLU-179-X",
            marca = "Fluke",
            especificaciones = "True-RMS, pantalla retroiluminada, medición de temperatura.",
            accesorios = "Puntas de prueba, Sonda de temperatura, Estuche.",
            ubicacion = "Almacén General - Sección Electrónica",
            imageUrl = "https://images.unsplash.com/photo-1590333746438-28358378d381?w=800"
        ),
        Equipo(
            id = 5,
            nombre = "Osciloscopio Rigol",
            categoria = CategoriaEquipo.ELECTRONICA,
            estado = EstadoEquipo.DISPONIBLE,
            numSerie = "RIG-1054-Z",
            marca = "Rigol",
            especificaciones = "Digital, 50MHz, 4 canales, frecuencia de muestreo 1GSa/s.",
            accesorios = "4 Sondas, Cable de alimentación, Cable USB.",
            ubicacion = "Laboratorio de Electrónica - Banco 4",
            imageUrl = "https://images.unsplash.com/photo-1628144501257-25916f72873d?w=800"
        ),
        Equipo(
            id = 6,
            nombre = "Cámara Sony Alpha",
            categoria = CategoriaEquipo.AUDIOVISUAL,
            estado = EstadoEquipo.RESERVADO,
            numSerie = "SONY-A7III",
            marca = "Sony",
            especificaciones = "Mirrorless Full Frame, 24.2 MP, Grabación 4K.",
            accesorios = "Lente 28-70mm, 2 Baterías, Tarjeta SD 64GB.",
            ubicacion = "Estudio Audiovisual - Casillero 2",
            imageUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800"
        ),
        Equipo(
            id = 7,
            nombre = "Tablet Samsung S7",
            categoria = CategoriaEquipo.COMPUTO,
            estado = EstadoEquipo.PRESTADO,
            numSerie = "SAM-S7-TAB",
            marca = "Samsung",
            especificaciones = "Pantalla 11\", 128GB Almacenamiento, S-Pen incluido.",
            accesorios = "Funda teclado, Cargador, S-Pen.",
            ubicacion = "Laboratorio de Cómputo - Estante B2",
            imageUrl = "https://images.unsplash.com/photo-1544244015-0cd4b3fe809e?w=800"
        )
    )

    private val solicitudes = mutableListOf(
        SolicitudPrestamo(
            id = 101,
            equipoId = 6,
            ambienteDestino = "Laboratorio 1",
            proposito = "Pruebas de red",
            duracionHoras = 2,
            estado = EstadoSolicitud.APROBADA
        ),
        SolicitudPrestamo(
            id = 102,
            equipoId = 7,
            ambienteDestino = "Aula 204",
            proposito = "Clase de diseño",
            duracionHoras = 4,
            estado = EstadoSolicitud.ENTREGADA
        )
    )
    
    private var usuarioLogueado: Usuario? = null

    // HU-10: Filtrar equipos operativos para el catálogo público
    override fun obtenerEquipos(): List<Equipo> = equipos.toList()

    override fun obtenerEquipo(id: Int): Equipo? = equipos.find { it.id == id }

    override fun obtenerSolicitudes(): List<SolicitudPrestamo> = solicitudes.toList()

    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? = solicitudes.find { it.id == id }

    override fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        val equipo = obtenerEquipo(solicitud.equipoId)
            ?: return Result.failure(Exception("Equipo no encontrado"))

        if (equipo.estado != EstadoEquipo.DISPONIBLE) {
            return Result.failure(Exception("El equipo no está disponible para préstamo"))
        }

        val index = equipos.indexOf(equipo)
        equipos[index] = equipo.copy(estado = EstadoEquipo.RESERVADO)
        
        solicitudes.add(solicitud)
        return Result.success(Unit)
    }

    override fun cancelarSolicitud(id: Int): Result<Unit> {
        val solicitud = obtenerSolicitud(id)
            ?: return Result.failure(Exception("Solicitud no encontrada"))

        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return Result.failure(Exception("Solo se pueden cancelar solicitudes en estado SOLICITADA"))
        }

        val equipo = obtenerEquipo(solicitud.equipoId)
        if (equipo != null && equipo.estado == EstadoEquipo.RESERVADO) {
            val index = equipos.indexOf(equipo)
            equipos[index] = equipo.copy(estado = EstadoEquipo.DISPONIBLE)
        }

        val indexSolicitud = solicitudes.indexOf(solicitud)
        solicitudes[indexSolicitud] = solicitud.copy(estado = EstadoSolicitud.CANCELADA)

        return Result.success(Unit)
    }

    override fun login(correo: String): Result<Usuario> {
        // Simulación de usuario institucional con roles
        val esAdmin = correo.startsWith("admin")
        val mockUsuario = Usuario(
            nombre = if (esAdmin) "Administrador CTMA" else "Thomas Isaza",
            documento = if (esAdmin) "12345678" else "1000123456",
            ficha = if (esAdmin) "ADMIN" else "2558741",
            telefono = "3001234567",
            correoInstitucional = correo,
            correoAlternativo = "usuario@gmail.com",
            rol = if (esAdmin) RolUsuario.ADMINISTRADOR else RolUsuario.APRENDIZ
        )
        usuarioLogueado = mockUsuario
        return Result.success(mockUsuario)
    }

    override fun actualizarPerfil(usuario: Usuario): Result<Unit> {
        usuarioLogueado = usuario
        return Result.success(Unit)
    }

    override fun toggleFavorite(equipoId: Int): Result<Unit> {
        val index = equipos.indexOfFirst { it.id == equipoId }
        if (index != -1) {
            val equipo = equipos[index]
            equipos[index] = equipo.copy(isFavorite = !equipo.isFavorite)
            return Result.success(Unit)
        }
        return Result.failure(Exception("Equipo no encontrado"))
    }

    override fun obtenerUsuarioLogueado(): Usuario? = usuarioLogueado

    // Admin Methods (HU-09 a HU-16)

    override fun registrarEquipo(equipo: Equipo): Result<Unit> {
        // HU-09: No permite registrar dos equipos con el mismo número de serie
        if (equipos.any { it.numSerie == equipo.numSerie }) {
            return Result.failure(Exception("Ya existe un equipo con el número de serie ${equipo.numSerie}"))
        }
        equipos.add(equipo.copy(id = equipos.size + 1))
        return Result.success(Unit)
    }

    override fun actualizarEstadoEquipo(id: Int, estado: EstadoEquipo): Result<Unit> {
        val index = equipos.indexOfFirst { it.id == id }
        if (index != -1) {
            equipos[index] = equipos[index].copy(estado = estado)
            return Result.success(Unit)
        }
        return Result.failure(Exception("Equipo no encontrado"))
    }

    override fun aprobarSolicitud(id: Int): Result<Unit> {
        val index = solicitudes.indexOfFirst { it.id == id }
        if (index != -1) {
            val solicitud = solicitudes[index]
            solicitudes[index] = solicitud.copy(estado = EstadoSolicitud.APROBADA)
            
            // Al aprobar, el equipo ya está RESERVADO. Cuando se entrega pasará a PRESTADO.
            return Result.success(Unit)
        }
        return Result.failure(Exception("Solicitud no encontrada"))
    }

    override fun rechazarSolicitud(id: Int, justificacion: String): Result<Unit> {
        val index = solicitudes.indexOfFirst { it.id == id }
        if (index != -1) {
            val solicitud = solicitudes[index]
            solicitudes[index] = solicitud.copy(
                estado = EstadoSolicitud.RECHAZADA,
                justificacionRechazo = justificacion
            )
            
            // Liberar equipo
            val equipo = obtenerEquipo(solicitud.equipoId)
            if (equipo != null) {
                val eIndex = equipos.indexOf(equipo)
                equipos[eIndex] = equipo.copy(estado = EstadoEquipo.DISPONIBLE)
            }
            
            return Result.success(Unit)
        }
        return Result.failure(Exception("Solicitud no encontrada"))
    }

    override fun registrarDevolucion(id: Int, novedad: String?, gravedad: NivelGravedad?): Result<Unit> {
        val index = solicitudes.indexOfFirst { it.id == id }
        if (index != -1) {
            val solicitud = solicitudes[index]
            
            // HU-12: Marcar como COMPLETADA
            solicitudes[index] = solicitud.copy(
                estado = EstadoSolicitud.COMPLETADA,
                novedadDevolucion = novedad,
                gravedadNovedad = gravedad
            )
            
            // HU-12: Actualiza inmediatamente el estado del equipo a DISPONIBLE
            val equipo = obtenerEquipo(solicitud.equipoId)
            if (equipo != null) {
                val eIndex = equipos.indexOf(equipo)
                equipos[eIndex] = equipo.copy(estado = EstadoEquipo.DISPONIBLE)
            }
            
            return Result.success(Unit)
        }
        return Result.failure(Exception("Solicitud no encontrada"))
    }

    override fun obtenerTrazabilidad(equipoId: Int): List<SolicitudPrestamo> {
        // HU-16: Cronología de préstamos por equipo
        return solicitudes.filter { it.equipoId == equipoId }
            .sortedByDescending { it.fechaSolicitud }
    }

    override fun obtenerEstadisticas(): ReporteEstadistico {
        // HU-15: Métricas básicas
        val masSolicitado = solicitudes.groupBy { it.equipoId }
            .maxByOrNull { it.value.size }
            ?.key?.let { id -> obtenerEquipo(id)?.nombre } ?: "N/A"
            
        val usoPorCat = solicitudes.groupBy { 
            obtenerEquipo(it.equipoId)?.categoria ?: CategoriaEquipo.OTROS 
        }.mapValues { it.value.size }

        return ReporteEstadistico(
            equipoMasSolicitado = masSolicitado,
            prestamosTotales = solicitudes.size,
            horasPico = "08:00 - 10:00", // Simulado
            usoPorCategoria = usoPorCat
        )
    }
}
