package com.ctma.prestamolab.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ctma.prestamolab.model.EstadoSolicitud

// [HU 02] Registrar Solicitud de Préstamo
// [HU 11] Aprobación y Rechazo de Solicitudes
// [HU 13] Registro de Novedades y Daños
@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: EstadoSolicitud,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val justificacionRechazo: String? = null,
    val novedades: String? = null
)
