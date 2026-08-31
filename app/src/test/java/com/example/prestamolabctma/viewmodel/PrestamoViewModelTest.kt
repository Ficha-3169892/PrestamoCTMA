package com.example.prestamolabctma.viewmodel

import com.example.prestamolabctma.data.repository.InMemoryPrestamoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrestamoViewModelTest {

    private lateinit var viewModel: PrestamoViewModel
    private lateinit var repository: InMemoryPrestamoRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = InMemoryPrestamoRepository()
        viewModel = PrestamoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
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
        assertEquals(7, viewModel.uiState.value.equipos.size)
    }

    @Test
    fun `HU-05 - Validar correo institucional SENA`() {
        // Fallo: Dominio incorrecto
        viewModel.login("usuario@gmail.com")
        assertEquals("Error: Use un correo @soy.sena.edu.co o @sena.edu.co", viewModel.uiState.value.errorFormulario)

        // Éxito: Dominio institucional
        viewModel.login("estudiante@soy.sena.edu.co")
        
        // Ejecutar coroutines pendientes
        testDispatcher.scheduler.runCurrent()
        
        assertNotNull(viewModel.uiState.value.usuarioLogueado)
        assertEquals("estudiante@soy.sena.edu.co", viewModel.uiState.value.usuarioLogueado?.correoInstitucional)
    }
}
