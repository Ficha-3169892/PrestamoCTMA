package com.example.prestamolabctma.model

data class ReporteEstadistico(
    val equipoMasSolicitado: String,
    val prestamosTotales: Int,
    val horasPico: String,
    val usoPorCategoria: Map<CategoriaEquipo, Int>,
)
