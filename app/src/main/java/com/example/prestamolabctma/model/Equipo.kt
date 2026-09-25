package com.example.prestamolabctma.model

@Suppress("unused")
data class Equipo(
    val id: Int,
    val nombre: String,
    val categoria: CategoriaEquipo,
    val estado: EstadoEquipo,
    val isFavorite: Boolean = false,
    val numSerie: String = "",
    val marca: String = "",
    val especificaciones: String = "",
    val accesorios: String = "",
    val ubicacion: String = "",
    val imageUrl: String = "",
)
