package com.ctma.prestamolab.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ctma.prestamolab.ui.navigation.Rutas
import com.ctma.prestamolab.ui.screens.*
import com.ctma.prestamolab.ui.viewmodel.AuthViewModel
import com.ctma.prestamolab.ui.viewmodel.PrestamoViewModel

/**
 * [HU General] Orquestador de navegación y flujo principal.
 * Integra los módulos de Aprendiz y Administración (Semana 07).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestamoLabApp(
    viewModel: PrestamoViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authState.usuario) {
        viewModel.setUsuario(authState.usuario)
    }

    LaunchedEffect(uiState.mensaje, authState.mensaje) {
        val mensaje = uiState.mensaje ?: authState.mensaje
        if (mensaje != null) {
            snackbarHostState.showSnackbar(mensaje)
            viewModel.limpiarMensaje()
            authViewModel.limpiarMensaje()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (authState.usuario == null) {
            LoginScreen(
                state = authState,
                onLogin = { correo, pass -> authViewModel.iniciarSesion(correo, pass) },
            )
        } else {
            NavHost(
                navController = navController,
                startDestination = Rutas.CATALOGO,
                modifier = Modifier.padding(padding)
            ) {
                composable(Rutas.CATALOGO) {
                    CatalogoScreen(
                        equiposState = uiState.equiposState,
                        busqueda = uiState.busqueda,
                        categoriaSeleccionada = uiState.categoriaSeleccionada,
                        esAdministrador = authState.usuario?.esAdministrador ?: false,
                        onBusquedaChange = { viewModel.buscar(it) },
                        onCategoriaChange = { viewModel.filtrarPorCategoria(it) },
                        onEquipoClick = { navController.navigate(Rutas.equipoDetalle(it)) },
                        onMisSolicitudesClick = { navController.navigate(Rutas.MIS_SOLICITUDES) },
                        onGestionAdminClick = { navController.navigate(Rutas.GESTION_ADMIN) },
                        onEstadisticasClick = { navController.navigate(Rutas.ESTADISTICAS) },
                        onInventarioClick = { navController.navigate(Rutas.INVENTARIO) },
                        onPerfilClick = { navController.navigate(Rutas.PERFIL) }
                    ) { viewModel.conmutarFavorito(it) }
                }

                composable(Rutas.PERFIL) {
                    PerfilScreen(
                        usuario = authState.usuario,
                        onBack = { navController.popBackStack() },
                        onActualizar = { tel, correo -> authViewModel.actualizarContacto(tel, correo) },
                        onCerrarSesion = { authViewModel.cerrarSesion() },
                    )
                }

                composable(Rutas.INVENTARIO) {
                    InventarioScreen(
                        onAgregarEquipo = { viewModel.agregarEquipo(it) { navController.popBackStack() } },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Rutas.GESTION_ADMIN) {
                    GestionSolicitudesAdminScreen(
                        solicitudesState = uiState.solicitudesState,
                        equipos = uiState.equipos,
                        onAprobar = { viewModel.aprobarSolicitud(it) },
                        onRechazar = { id, jus -> viewModel.rechazarSolicitud(id, jus) },
                        onDevolver = { id, nov -> viewModel.devolverEquipo(id, nov) },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Rutas.ESTADISTICAS) {
                    EstadisticasScreen(
                        stats = uiState.estadisticas,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Rutas.EQUIPO_DETALLE,
                    arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
                ) { entry ->
                    val equipoId = entry.arguments?.getInt("equipoId") ?: -1
                    EquipoDetalleScreen(
                        equipo = viewModel.obtenerEquipo(equipoId),
                        esAdministrador = authState.usuario?.esAdministrador ?: false,
                        onBack = { navController.popBackStack() },
                        onSolicitarClick = { navController.navigate(Rutas.solicitar(equipoId)) },
                        onTrazabilidadClick = { navController.navigate(Rutas.trazabilidad(equipoId)) }
                    )
                }

                composable(
                    route = Rutas.SOLICITAR,
                    arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
                ) { entry ->
                    val equipoId = entry.arguments?.getInt("equipoId") ?: -1
                    SolicitarScreen(
                        equipo = viewModel.obtenerEquipo(equipoId),
                        errores = uiState.erroresSolicitud,
                        guardando = uiState.guardando,
                        onBack = { navController.popBackStack() },
                        onGuardar = { ambiente, proposito, duracion ->
                            val usuarioId = authState.usuario?.id ?: 0
                            viewModel.crearSolicitud(usuarioId, equipoId, ambiente, proposito, duracion) { solicitudId ->
                                navController.navigate(Rutas.solicitudDetalle(solicitudId)) {
                                    popUpTo(Rutas.CATALOGO)
                                }
                            }
                        }
                    )
                }

                composable(Rutas.MIS_SOLICITUDES) {
                    MisSolicitudesScreen(
                        solicitudesState = uiState.solicitudesState,
                        equipos = uiState.equipos,
                        onBack = { navController.popBackStack() },
                        onSolicitudClick = { navController.navigate(Rutas.solicitudDetalle(it)) }
                    )
                }

                composable(
                    route = Rutas.TRAZABILIDAD,
                    arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
                ) { entry ->
                    val equipoId = entry.arguments?.getInt("equipoId") ?: -1
                    LaunchedEffect(equipoId) { viewModel.cargarTrazabilidad(equipoId) }
                    TrazabilidadScreen(
                        equipo = viewModel.obtenerEquipo(equipoId),
                        historialState = uiState.trazabilidadState,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Rutas.SOLICITUD_DETALLE,
                    arguments = listOf(navArgument("solicitudId") { type = NavType.IntType })
                ) { entry ->
                    val solicitudId = entry.arguments?.getInt("solicitudId") ?: -1
                    SolicitudDetalleScreen(
                        solicitud = viewModel.obtenerSolicitud(solicitudId),
                        equipo = viewModel.obtenerSolicitud(solicitudId)?.let { viewModel.obtenerEquipo(it.equipoId) },
                        alertaActiva = uiState.alertaActiva,
                        onBack = { navController.popBackStack() },
                        onCancelar = { viewModel.cancelarSolicitud(solicitudId) }
                    )
                }
            }
        }
    }
}
