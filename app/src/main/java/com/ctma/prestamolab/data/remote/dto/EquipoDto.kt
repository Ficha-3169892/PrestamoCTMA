package com.ctma.prestamolab.data.remote.dto

import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para la entidad Equipo en Supabase.
 * Usa @SerialName para coincidir con los nombres de columna en Postgres (minúsculas).
 * Todos los campos tienen valores por defecto para ser altamente tolerantes a esquemas parciales en Supabase.
 */
@Serializable
data class EquipoDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("nombre") val nombre: String = "Equipo sin nombre",
    @SerialName("serie") val serie: String? = null,
    @SerialName("marca") val marca: String? = null,
    @SerialName("categoria") val categoria: CategoriaEquipo = CategoriaEquipo.HERRAMIENTA,
    @SerialName("estado") val estado: EstadoEquipo = EstadoEquipo.DISPONIBLE,
    @SerialName("descripcion") val descripcion: String? = null,
    @SerialName("especificaciones") val especificaciones: String? = null,
    @SerialName("accesorios") val accesorios: List<String> = emptyList(),
    @SerialName("ubicacion") val ubicacion: String? = null,
    @SerialName("imagenurl") val imagenUrl: String? = null,
    @SerialName("esfavorito") val esFavorito: Boolean = false
)

fun EquipoDto.toDomain(): Equipo {
    val idVal = id ?: 0
    val especificacionesFinal = especificaciones ?: descripcion ?: "Sin descripción"
    val serieFinal = if (serie.isNullOrBlank()) "SN-$idVal" else serie
    val marcaFinal = if (marca.isNullOrBlank()) "SENA" else marca
    val ubicacionFinal = if (ubicacion.isNullOrBlank()) "Laboratorio" else ubicacion

    return Equipo(
        id = idVal,
        nombre = nombre,
        serie = serieFinal,
        marca = marcaFinal,
        categoria = categoria,
        estado = estado,
        especificaciones = especificacionesFinal,
        accesorios = accesorios,
        ubicacion = ubicacionFinal,
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
        descripcion = especificaciones,
        especificaciones = especificaciones,
        accesorios = accesorios,
        ubicacion = ubicacion,
        imagenUrl = imagenUrl,
        esFavorito = esFavorito
    )
}
