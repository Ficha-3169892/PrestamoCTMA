package com.ctma.prestamolab.data.remote.dto

import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import kotlinx.serialization.Serializable

/**
 * DTO para la entidad Equipo en Supabase.
 * Utiliza camelCase para coincidir con el SDK de Supabase si se configura así, 
 * o se puede usar @SerialName si la tabla usa snake_case.
 */
@Serializable
data class EquipoDto(
    val id: Int? = null,
    val nombre: String,
    val serie: String,
    val marca: String,
    val categoria: CategoriaEquipo,
    val estado: EstadoEquipo,
    val especificaciones: String,
    val accesorios: List<String>,
    val ubicacion: String,
    val imagenUrl: String? = null,
    val esFavorito: Boolean = false
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
