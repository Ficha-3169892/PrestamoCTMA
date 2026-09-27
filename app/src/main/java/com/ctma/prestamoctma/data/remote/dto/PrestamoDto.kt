package com.ctma.prestamoctma.data.remote.dto

import com.ctma.prestamoctma.data.local.entities.PrestamoEntity
import com.ctma.prestamoctma.model.EstadoSolicitud
import com.ctma.prestamoctma.model.GravedadDano
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class PrestamoDto(
    val id: String,
    val equipo_id: String,
    val nombre_equipo: String,
    val usuario_id: String,
    val ambiente_destino: String,
    val proposito: String,
    val duracion_horas: Int,
    val fecha_solicitud: Long,
    val estado: String,
    val novedad_detalle: String?,
    val gravedad_dano: String
)

fun PrestamoDto.asEntity() = PrestamoEntity(
    id = id,
    equipoId = equipo_id,
    nombreEquipo = nombre_equipo,
    usuarioId = usuario_id,
    ambienteDestino = ambiente_destino,
    proposito = proposito,
    duracionHoras = duracion_horas,
    fechaSolicitud = Date(fecha_solicitud),
    estado = EstadoSolicitud.valueOf(estado),
    novedadDetalle = novedad_detalle,
    gravedadDano = GravedadDano.valueOf(gravedad_dano)
)

fun PrestamoEntity.asDto() = PrestamoDto(
    id = id,
    equipo_id = equipoId,
    nombre_equipo = nombreEquipo,
    usuario_id = usuarioId,
    ambiente_destino = ambienteDestino,
    proposito = proposito,
    duracion_horas = duracionHoras,
    fecha_solicitud = fechaSolicitud.time,
    estado = estado.name,
    novedad_detalle = novedadDetalle,
    gravedad_dano = gravedadDano.name
)
