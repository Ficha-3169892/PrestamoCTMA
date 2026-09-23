package com.ctma.prestamolab.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// [HU 05] Gestión de Roles de Usuarios (Administrador / Aprendiz)
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val documento: String,
    val ficha: String,
    val telefono: String,
    val correoInstitucional: String,
    val correoAlternativo: String? = null,
    val esAdministrador: Boolean = false
)
