package com.example.prestamolabctma.viewmodel

import com.example.prestamolabctma.data.repository.InMemoryPrestamoRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class PrestamoViewModelTest {

    private lateinit var viewModel: PrestamoViewModel
    private lateinit var repository: InMemoryPrestamoRepository

    @Before
    fun setup() {
        repository = InMemoryPrestamoRepository()
        viewModel = PrestamoViewModel(repository)
    }

    @Test
    fun `RN-02 - Validar ambiente obligatorio`() {
        viewModel.registrarSolicitud(1, "", "Propósito válido largo", 2)
        assertEquals("El ambiente o destino es obligatorio", viewModel.uiState.value.errorFormulario)
    }

    @Test
    fun `RN-03 - Validar proposito longitud minima`() {
        viewModel.registrarSolicitud(1, "Aula 101", "Corto", 2)
        assertEquals("El propósito debe tener entre 10 y 180 caracteres", viewModel.uiState.value.errorFormulario)
    }

    @Test
    fun `RN-04 - Validar duracion fuera de rango`() {
        viewModel.registrarSolicitud(1, "Aula 101", "Propósito válido largo", 10)
        assertEquals("La duración debe estar entre 1 y 8 horas", viewModel.uiState.value.errorFormulario)
    }

    @Test
    fun `Carga inicial de equipos`() {
        assertNotNull(viewModel.uiState.value.equipos)
        assertEquals(5, viewModel.uiState.value.equipos.size)
    }
}
