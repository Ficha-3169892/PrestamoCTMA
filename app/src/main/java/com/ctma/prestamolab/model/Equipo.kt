package com.ctma.prestamolab.model

data class Equipo(
    val id: Int,
    val nombre: String,
    val serie: String,
    val marca: String,
    val categoria: CategoriaEquipo,
    val estado: EstadoEquipo,
    val especificaciones: String,
    val accesorios: List<String>,
    val ubicacion: String,
    val imagenUrl: String? = null,
    val esFavorito: Boolean = false,
)
