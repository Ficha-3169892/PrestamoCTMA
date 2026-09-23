package com.example.prestamolabctma.viewmodel

import com.example.prestamolabctma.data.repository.InMemoryPrestamoRepository
import com.example.prestamolabctma.model.CategoriaEquipo
import com.example.prestamolabctma.model.EstadoEquipo
import com.example.prestamolabctma.model.EstadoSolicitud
import com.example.prestamolabctma.model.NivelGravedad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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

    // =================================────────────────========================
    // STEVEN123SI: HU-01 a HU-04 (#1 a #4 en GitHub Issues)
    // =================================────────────────========================

    @Test
    fun `HU-01 Consulta de Catalogo de Equipos`() {
        val equipos = viewModel.uiState.value.equipos
        assertNotNull(equipos)
        assertTrue(equipos.isNotEmpty())
        assertTrue(equipos.all { it.estado != EstadoEquipo.FUERA_DE_SERVICIO })
    }

    @Test
    fun `HU-02 Registrar Solicitud de Prestamo`() {
        // Validaciones de negocio (RN-02, RN-03, RN-04)
        // 1. Ambiente obligatorio
        viewModel.registrarSolicitud(1, "", "Propósito válido para práctica", 2)
        assertEquals("El ambiente o destino es obligatorio", viewModel.uiState.value.errorFormulario)

        // 2. Propósito min 10 caracteres
        viewModel.registrarSolicitud(1, "Lab 201", "Corto", 2)
        assertEquals("El propósito debe tener entre 10 y 180 caracteres", viewModel.uiState.value.errorFormulario)

        // 3. Duración de 1 a 8 horas
        viewModel.registrarSolicitud(1, "Lab 201", "Propósito válido para práctica", 12)
        assertEquals("La duración debe estar entre 1 y 8 horas", viewModel.uiState.value.errorFormulario)

        // 4. Caso Éxito
        viewModel.registrarSolicitud(1, "Lab 201", "Propósito válido para práctica", 3)
        testDispatcher.scheduler.runCurrent()
        assertNull(viewModel.uiState.value.errorFormulario)
    }

    @Test
    fun `HU-03 Gestion y Cancelacion de Solicitudes`() {
        viewModel.login("estudiante@soy.sena.edu.co")
        testDispatcher.scheduler.runCurrent()

        viewModel.registrarSolicitud(1, "Lab 102", "Uso para taller de electrónica", 2)
        testDispatcher.scheduler.runCurrent()

        val solicitudCreada = viewModel.uiState.value.solicitudes.lastOrNull()
        assertNotNull(solicitudCreada)

        solicitudCreada?.let {
            viewModel.cancelarPrestamo(it.id)
            testDispatcher.scheduler.runCurrent()
            val solicitudCancelada = viewModel.uiState.value.solicitudes.find { s -> s.id == it.id }
            assertEquals(EstadoSolicitud.CANCELADA, solicitudCancelada?.estado)
        }
    }

    @Test
    fun `HU-04 Consultar Detalle y Ficha Tecnica de un Equipo`() {
        val equipo = repository.obtenerEquipo(1)
        assertNotNull(equipo)
        assertEquals(1, equipo?.id)
        assertNotNull(equipo?.marca)
        assertNotNull(equipo?.numSerie)
        assertNotNull(equipo?.especificaciones)
    }

    // =================================────────────────========================
    // THOMASISAZA04 (TÚ): HU-05 a HU-08 (#6 a #9 en GitHub Issues)
    // =================================────────────────========================

    @Test
    fun `HU-05 Inicio de Sesion con Correo Institucional`() {
        // Fallo: Correo no institucional
        viewModel.login("usuario@gmail.com")
        assertEquals("Error: Use un correo @soy.sena.edu.co o @sena.edu.co", viewModel.uiState.value.errorFormulario)

        // Éxito: Correo @soy.sena.edu.co
        viewModel.login("estudiante@soy.sena.edu.co")
        testDispatcher.scheduler.runCurrent()

        assertNotNull(viewModel.uiState.value.usuarioLogueado)
        assertEquals("estudiante@soy.sena.edu.co", viewModel.uiState.value.usuarioLogueado?.correoInstitucional)
    }

    @Test
    fun `HU-06 Gestion de Perfil de Usuario`() {
        viewModel.login("estudiante@soy.sena.edu.co")
        testDispatcher.scheduler.runCurrent()

        viewModel.actualizarPerfil("3001234567", "estudiante.alt@gmail.com")
        testDispatcher.scheduler.runCurrent()

        val usuario = viewModel.uiState.value.usuarioLogueado
        assertNotNull(usuario)
        assertEquals("3001234567", usuario?.telefono)
        assertEquals("estudiante.alt@gmail.com", usuario?.correoAlternativo)
    }

    @Test
    fun `HU-07 Busqueda y Filtro Avanzado de Equipos`() {
        viewModel.actualizarBusqueda("Osciloscopio")
        testDispatcher.scheduler.runCurrent()
        val resultadosTexto = viewModel.equiposFiltrados.value
        assertTrue(resultadosTexto.all { it.nombre.contains("Osciloscopio", ignoreCase = true) })

        viewModel.seleccionarCategoria(CategoriaEquipo.ELECTRONICA)
        testDispatcher.scheduler.runCurrent()
        val resultadosCategoria = viewModel.equiposFiltrados.value
        assertTrue(resultadosCategoria.all { it.categoria == CategoriaEquipo.ELECTRONICA })
    }

    @Test
    fun `HU-08 Marcado de Equipos Frecuentes`() {
        val equipoId = 1
        val equipoInicial = repository.obtenerEquipo(equipoId)
        val esFavoritoInicial = equipoInicial?.isFavorite ?: false

        viewModel.toggleFavorito(equipoId)
        testDispatcher.scheduler.runCurrent()

        val equipoModificado = repository.obtenerEquipo(equipoId)
        assertEquals(!esFavoritoInicial, equipoModificado?.isFavorite)
    }

    // =================================────────────────========================
    // LINEY042-ALT: HU-09 a HU-12 (#10 a #13 en GitHub Issues)
    // =========================================================================

    @Test
    fun `HU-09 Registro de Nuevos Equipos en Inventario`() {
        viewModel.registrarNuevoEquipo(
            nombre = "Multímetro Digital Pro",
            serie = "FLK-9999",
            categoria = CategoriaEquipo.MEDICION,
            ubicacion = "Estante B",
            marca = "Fluke",
            specs = "RMS Verdadero 1000V",
            accesorios = "Puntas de prueba",
            url = "https://example.com/multimetro.png"
        )
        testDispatcher.scheduler.runCurrent()

        assertEquals("Equipo registrado con éxito", viewModel.uiState.value.mensaje)
    }

    @Test
    fun `HU-10 Actualizacion del Estado de Equipos`() {
        viewModel.cambiarEstadoOperativo(1, EstadoEquipo.MANTENIMIENTO)
        testDispatcher.scheduler.runCurrent()

        val equipoActualizado = repository.obtenerEquipo(1)
        assertEquals(EstadoEquipo.MANTENIMIENTO, equipoActualizado?.estado)
    }

    @Test
    fun `HU-11 Aprobacion y Rechazo de Solicitudes`() {
        viewModel.login("estudiante@soy.sena.edu.co")
        testDispatcher.scheduler.runCurrent()

        viewModel.registrarSolicitud(2, "Lab 202", "Práctica de redes de datos", 3)
        testDispatcher.scheduler.runCurrent()

        val solicitud = viewModel.uiState.value.solicitudes.last()

        viewModel.aprobarSolicitud(solicitud.id)
        testDispatcher.scheduler.runCurrent()

        val solicitudAprobada = viewModel.uiState.value.solicitudes.find { it.id == solicitud.id }
        assertEquals(EstadoSolicitud.APROBADA, solicitudAprobada?.estado)
    }

    @Test
    fun `HU-12 Confirmacion y Recepcion de Devoluciones`() {
        viewModel.login("estudiante@soy.sena.edu.co")
        testDispatcher.scheduler.runCurrent()

        viewModel.registrarSolicitud(3, "Lab 303", "Práctica de soldadura", 2)
        testDispatcher.scheduler.runCurrent()

        val solicitud = viewModel.uiState.value.solicitudes.last()
        viewModel.aprobarSolicitud(solicitud.id)
        testDispatcher.scheduler.runCurrent()

        viewModel.registrarRetorno(solicitud.id, novedad = null, gravedad = null)
        testDispatcher.scheduler.runCurrent()

        val solicitudDevuelta = viewModel.uiState.value.solicitudes.find { it.id == solicitud.id }
        assertEquals(EstadoSolicitud.COMPLETADA, solicitudDevuelta?.estado)

        val equipo = repository.obtenerEquipo(3)
        assertEquals(EstadoEquipo.DISPONIBLE, equipo?.estado)
    }

    // =================================────────────────========================
    // DURMAN-V: HU-13 a HU-16 (#14 a #17 en GitHub Issues)
    // =================================────────────────========================

    @Test
    fun `HU-13 Registro de Novedades y Danos`() {
        viewModel.login("estudiante@soy.sena.edu.co")
        testDispatcher.scheduler.runCurrent()

        viewModel.registrarSolicitud(4, "Lab 101", "Ensamblaje de circuitos", 2)
        testDispatcher.scheduler.runCurrent()

        val solicitud = viewModel.uiState.value.solicitudes.last()
        viewModel.aprobarSolicitud(solicitud.id)
        testDispatcher.scheduler.runCurrent()

        viewModel.registrarRetorno(solicitud.id, novedad = "Cable deteriorado", gravedad = NivelGravedad.LEVE)
        testDispatcher.scheduler.runCurrent()

        val solicitudConNovedad = viewModel.uiState.value.solicitudes.find { it.id == solicitud.id }
        assertEquals("Cable deteriorado", solicitudConNovedad?.novedadDevolucion)
    }

    @Test
    fun `HU-14 Notificaciones y Alertas de Vencimiento`() {
        viewModel.cargarDatos()
        testDispatcher.scheduler.runCurrent()

        assertNotNull(viewModel.uiState.value.solicitudesPorVencer)
        assertNotNull(viewModel.uiState.value.solicitudesVencidas)
    }

    @Test
    fun `HU-15 Generacion de Reportes de Uso del Laboratorio`() {
        viewModel.cargarEstadisticas()
        testDispatcher.scheduler.runCurrent()

        val reportes = viewModel.uiState.value.estadisticas
        assertNotNull(reportes)
    }

    @Test
    fun `HU-16 Historial de Trazabilidad y Auditoria de Equipos`() {
        viewModel.cargarTrazabilidad(1)
        testDispatcher.scheduler.runCurrent()

        val trazabilidad = viewModel.uiState.value.trazabilidadEquipo
        assertNotNull(trazabilidad)
    }
}
