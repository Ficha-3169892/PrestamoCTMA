package com.example.prestamolabctma.model

data class Usuario(
    val nombre: String,
    val documento: String,
    val ficha: String,
    val telefono: String,
    val correoInstitucional: String,
    val correoAlternativo: String,
    val rol: RolUsuario = RolUsuario.APRENDIZ,
)

enum class RolUsuario {
    APRENDIZ,
    ADMINISTRADOR
}
