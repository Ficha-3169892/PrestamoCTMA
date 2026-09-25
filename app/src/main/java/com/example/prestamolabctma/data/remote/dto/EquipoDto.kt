package com.example.prestamolabctma.data.remote.dto

import com.example.prestamolabctma.model.CategoriaEquipo
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.EstadoEquipo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EquipoDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("nombre") val nombre: String,
    @SerialName("serie") val serie: String,
    @SerialName("marca") val marca: String,
    @SerialName("categoria") val categoria: CategoriaEquipo,
    @SerialName("estado") val estado: EstadoEquipo,
    @SerialName("especificaciones") val especificaciones: String,
    @SerialName("accesorios") val accesorios: String = "",
    @SerialName("ubicacion") val ubicacion: String,
    @SerialName("imagenurl") val imagenUrl: String? = null,
    @SerialName("esfavorito") val esFavorito: Boolean = false
)

fun EquipoDto.toDomain(): Equipo {
    return Equipo(
        id = id ?: 0,
        nombre = nombre,
        numSerie = serie,
        marca = marca,
        categoria = categoria,
        estado = estado,
        especificaciones = especificaciones,
        accesorios = accesorios,
        ubicacion = ubicacion,
        imageUrl = imagenUrl ?: "",
        isFavorite = esFavorito
    )
}

fun Equipo.toDto(): EquipoDto {
    return EquipoDto(
        id = if (id == 0) null else id,
        nombre = nombre,
        serie = numSerie,
        marca = marca,
        categoria = categoria,
        estado = estado,
        especificaciones = especificaciones,
        accesorios = accesorios,
        ubicacion = ubicacion,
        imagenUrl = imageUrl,
        esFavorito = isFavorite
    )
}
