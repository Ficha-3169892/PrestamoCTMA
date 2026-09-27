package com.ctma.prestamoctma.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ctma.prestamoctma.model.EstadoSolicitud
import com.ctma.prestamoctma.model.GravedadDano
import com.ctma.prestamoctma.model.SolicitudPrestamo
import java.util.Date

@Entity(
    tableName = "prestamos",
    foreignKeys = [
        ForeignKey(
            entity = ArticuloEntity::class,
            parentColumns = ["id"],
            childColumns = ["equipoId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index(value = ["equipoId"])]
)
data class PrestamoEntity(
    @PrimaryKey
    val id: String,
    val equipoId: String,
    val nombreEquipo: String,
    val usuarioId: String,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val fechaSolicitud: Date,
    val estado: EstadoSolicitud,
    val novedadDetalle: String?,
    val gravedadDano: GravedadDano
)

@Suppress("unused")
fun PrestamoEntity.asExternalModel() = SolicitudPrestamo(
    id = id,
    equipoId = equipoId,
    nombreEquipo = nombreEquipo,
    usuarioId = usuarioId,
    ambienteDestino = ambienteDestino,
    proposito = proposito,
    duracionHoras = duracionHoras,
    fechaSolicitud = fechaSolicitud,
    estado = estado,
    novedadDetalle = novedadDetalle,
    gravedadDano = gravedadDano
)

@Suppress("unused")
fun SolicitudPrestamo.asEntity() = PrestamoEntity(
    id = id,
    equipoId = equipoId,
    nombreEquipo = nombreEquipo,
    usuarioId = usuarioId,
    ambienteDestino = ambienteDestino,
    proposito = proposito,
    duracionHoras = duracionHoras,
    fechaSolicitud = fechaSolicitud,
    estado = estado,
    novedadDetalle = novedadDetalle,
    gravedadDano = gravedadDano
)
