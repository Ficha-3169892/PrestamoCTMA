package com.ctma.prestamoctma.model

data class Equipo(
    val id: String,
    val nombre: String,
    val categoria: CategoriaEquipo,
    val estado: EstadoEquipo,
    val descripcion: String = ""
)
