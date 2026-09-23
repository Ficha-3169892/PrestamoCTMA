package com.ctma.prestamolab.data.remote.dto

import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para la entidad Equipo en Supabase.
 * Usa @SerialName para coincidir exactamente con los nombres de columna en Postgres (minúsculas).
 */
@Serializable
data class EquipoDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("nombre") val nombre: String,
    @SerialName("serie") val serie: String,
    @SerialName("marca") val marca: String,
    @SerialName("categoria") val categoria: CategoriaEquipo,
    @SerialName("estado") val estado: EstadoEquipo,
    @SerialName("especificaciones") val especificaciones: String,
    @SerialName("accesorios") val accesorios: List<String>,
    @SerialName("ubicacion") val ubicacion: String,
    @SerialName("imagenurl") val imagenUrl: String? = null,
    @SerialName("esfavorito") val esFavorito: Boolean = false
)

fun EquipoDto.toDomain(): Equipo {
    return Equipo(
        id = id ?: 0,
        nombre = nombre,
        serie = serie,
        marca = marca,
        categoria = categoria,
        estado = estado,
        especificaciones = especificaciones,
        accesorios = accesorios,
        ubicacion = ubicacion,
        imagenUrl = imagenUrl,
        esFavorito = esFavorito
    )
}

fun Equipo.toDto(): EquipoDto {
    return EquipoDto(
        id = if (id == 0) null else id,
        nombre = nombre,
        serie = serie,
        marca = marca,
        categoria = categoria,
        estado = estado,
        especificaciones = especificaciones,
        accesorios = accesorios,
        ubicacion = ubicacion,
        imagenUrl = imagenUrl,
        esFavorito = esFavorito
    )
}
