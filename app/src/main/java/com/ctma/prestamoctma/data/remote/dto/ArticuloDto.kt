package com.ctma.prestamoctma.data.remote.dto

import com.ctma.prestamoctma.data.local.entities.ArticuloEntity
import com.ctma.prestamoctma.model.CategoriaEquipo
import com.ctma.prestamoctma.model.EstadoEquipo
import kotlinx.serialization.Serializable

@Serializable
data class ArticuloDto(
    val id: String,
    val nombre: String,
    val categoria: String,
    val estado: String,
    val descripcion: String
)

fun ArticuloDto.asEntity() = ArticuloEntity(
    id = id,
    nombre = nombre,
    categoria = CategoriaEquipo.valueOf(categoria),
    estado = EstadoEquipo.valueOf(estado),
    descripcion = descripcion
)

fun ArticuloEntity.asDto() = ArticuloDto(
    id = id,
    nombre = nombre,
    categoria = categoria.name,
    estado = estado.name,
    descripcion = descripcion
)
