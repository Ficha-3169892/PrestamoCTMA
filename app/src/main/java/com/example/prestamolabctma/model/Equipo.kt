package com.example.prestamolabctma.model

@Suppress("unused")
data class Equipo(
    val id: Int,
    val nombre: String,
    val categoria: CategoriaEquipo,
    val estado: EstadoEquipo
)
