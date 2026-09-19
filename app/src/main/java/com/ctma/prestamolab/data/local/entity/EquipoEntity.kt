package com.ctma.prestamolab.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.EstadoEquipo

// [HU 09] Registro de Nuevos Equipos en Inventario
// [HU 04] Consultar Detalle y Ficha Técnica de un Equipo
@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val serie: String,
    val marca: String,
    val categoria: CategoriaEquipo,
    val estado: EstadoEquipo,
    val especificaciones: String,
    val accesorios: String, // Guardado como String delimitado por comas para simplificar el MVP
    val ubicacion: String,
    val imagenUrl: String? = null,
    val esFavorito: Boolean = false
)
