package com.ctma.prestamoctma.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ctma.prestamoctma.model.CategoriaEquipo
import com.ctma.prestamoctma.model.Equipo
import com.ctma.prestamoctma.model.EstadoEquipo

@Entity(tableName = "articulos")
data class ArticuloEntity(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val categoria: CategoriaEquipo,
    val estado: EstadoEquipo,
    val descripcion: String,
)

@Suppress("unused")
fun ArticuloEntity.asExternalModel() = Equipo(
    id = id,
    nombre = nombre,
    categoria = categoria,
    estado = estado,
    descripcion = descripcion
)

@Suppress("unused")
fun Equipo.asEntity() = ArticuloEntity(
    id = id,
    nombre = nombre,
    categoria = categoria,
    estado = estado,
    descripcion = descripcion
)
