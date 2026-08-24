package com.ctma.prestamolab.util

// RN-02: El ambiente no puede estar vacío
fun ambienteValido(texto: String): Boolean = texto.trim().isNotBlank()

// RN-03: Propósito entre 10 y 180 caracteres
fun propositoValido(texto: String): Boolean = texto.trim().length in 10..180

// RN-04: Duración entre 1 y 8 horas
fun duracionValida(horas: Int): Boolean = horas in 1..8