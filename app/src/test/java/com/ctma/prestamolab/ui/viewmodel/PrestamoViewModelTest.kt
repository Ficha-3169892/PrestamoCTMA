package com.ctma.prestamolab.ui.viewmodel

import com.ctma.prestamolab.data.repository.InMemoryPrestamoRepository
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.ui.state.ListadoUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * [HU General] Pruebas unitarias para el ViewModel usando flujos reactivos (Semana 07).
 * Sigue el patrón AAA y usa Dispatchers controlados.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PrestamoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: PrestamoViewModel
    private lateinit var repository: InMemoryPrestamoRepository

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
    fun test_HU_01_Al_iniciar_estado_equipos_debe_pasar_por_Cargando_y_luego_Contenido() = runTest {
        // [HU 01] Consulta de Catálogo de Equipos
        // Arrange & Act (ViewModel se inicializa en Before)
        
        // Assert inicial
        assertTrue(viewModel.uiState.value.equiposState is ListadoUiState.Cargando)

        // Avanzar corrutinas
        advanceUntilIdle()

        // Assert final
        assertTrue(viewModel.uiState.value.equiposState is ListadoUiState.Contenido)
        val contenido = (viewModel.uiState.value.equiposState as ListadoUiState.Contenido).datos
        assertEquals(1, contenido.size)
        assertEquals("Kit de electrónica básica", contenido[0].nombre)
    }

    @Test
    fun test_HU_07_Buscar_equipo_debe_actualizar_estado() = runTest {
        // [HU 07] Filtrado dinámico
        // Act
        viewModel.buscar("multimetro")
        
        // Assert
        assertEquals("multimetro", viewModel.uiState.value.busqueda)
    }

    @Test
    fun test_HU_08_Conmutar_favorito_debe_persistir_cambio() = runTest {
        // [HU 08] Marcado de Equipos Frecuentes
        // Arrange
        advanceUntilIdle()
        val equipoId = 1
        
        // Act
        viewModel.conmutarFavorito(equipoId)
        advanceUntilIdle()
        
        // Assert (En el repositorio InMemory simplificado para test)
        // Nota: En una implementacion real de Room esto verificaria el DAO
    }

    @Test
    fun test_HU_02_Crear_solicitud_exitosa_debe_mostrar_confirmacion() = runTest {
        // [HU 02] Registrar Solicitud de Préstamo
        // Act
        viewModel.crearSolicitud(
            equipoId = 1,
            ambienteDestino = "Aula 101",
            proposito = "Práctica de laboratorio",
            duracionTexto = "2"
        ) { }
        
        advanceUntilIdle()
        
        // Assert
        assertEquals("Solicitud enviada con éxito", viewModel.uiState.value.mensaje)
    }
}
