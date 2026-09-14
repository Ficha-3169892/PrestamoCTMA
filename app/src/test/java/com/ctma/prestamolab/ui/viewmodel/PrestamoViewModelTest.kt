package com.ctma.prestamolab.ui.viewmodel

import com.ctma.prestamolab.data.repository.InMemoryPrestamoRepository
import com.ctma.prestamolab.model.CategoriaEquipo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
    fun cargar_datos_iniciales_al_crear_viewmodel() {
        val state = viewModel.uiState.value
        assertFalse(state.equipos.isEmpty())
        assertTrue(state.solicitudes.isEmpty())
    }

    @Test
    fun buscar_actualiza_el_estado_de_busqueda() {
        viewModel.buscar("multimetro")
        assertEquals("multimetro", viewModel.uiState.value.busqueda)
    }

    @Test
    fun filtrar_por_categoria_actualiza_el_estado() {
        viewModel.filtrarPorCategoria(CategoriaEquipo.MEDICION)
        assertEquals(CategoriaEquipo.MEDICION, viewModel.uiState.value.categoriaSeleccionada)
        
        viewModel.filtrarPorCategoria(null)
        assertEquals(null, viewModel.uiState.value.categoriaSeleccionada)
    }

    @Test
    fun conmutar_favorito_actualiza_el_equipo_en_el_estado() {
        val equipoId = 1
        val esFavoritoInicial = viewModel.obtenerEquipo(equipoId)?.esFavorito ?: false
        
        viewModel.conmutarFavorito(equipoId)
        
        val esFavoritoFinal = viewModel.obtenerEquipo(equipoId)?.esFavorito
        assertEquals(!esFavoritoInicial, esFavoritoFinal)
    }

    @Test
    fun crear_solicitud_valida_actualiza_solicitudes_y_limpia_errores() {
        val equipoId = 1 // Disponible
        var solicitudIdCreada = -1
        
        viewModel.crearSolicitud(
            equipoId = equipoId,
            ambienteDestino = "Aula 201",
            proposito = "Practica de circuitos basicos",
            duracionTexto = "2"
        ) { solicitudIdCreada = it }

        assertTrue(solicitudIdCreada != -1)
        assertEquals(1, viewModel.uiState.value.solicitudes.size)
        assertFalse(viewModel.uiState.value.erroresSolicitud.hayErrores)
    }

    @Test
    fun crear_solicitud_invalida_no_llama_al_repositorio_y_muestra_errores() {
        viewModel.crearSolicitud(
            equipoId = 1,
            ambienteDestino = "", // Invalido
            proposito = "Corto", // Invalido (< 10)
            duracionTexto = "10" // Invalido (> 8)
        ) { }

        assertTrue(viewModel.uiState.value.solicitudes.isEmpty())
        assertTrue(viewModel.uiState.value.erroresSolicitud.hayErrores)
        assertEquals("Revisa los campos marcados antes de guardar.", viewModel.uiState.value.mensaje)
    }
}
