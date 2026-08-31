package com.ctma.prestamolab.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val documento: String,
    val ficha: String,
    val telefono: String,
    val correoInstitucional: String,
    val correoAlternativo: String? = null,
    val esAdministrador: Boolean = false,
)
