package com.ctma.prestamolab.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ctma.prestamolab.ui.navigation.Rutas
import com.ctma.prestamolab.ui.screens.CatalogoScreen
import com.ctma.prestamolab.ui.screens.EquipoDetalleScreen
import com.ctma.prestamolab.ui.screens.EstadisticasScreen
import com.ctma.prestamolab.ui.screens.GestionSolicitudesAdminScreen
import com.ctma.prestamolab.ui.screens.InventarioScreen
import com.ctma.prestamolab.ui.screens.LoginScreen
import com.ctma.prestamolab.ui.screens.MisSolicitudesScreen
import com.ctma.prestamolab.ui.screens.PerfilScreen
import com.ctma.prestamolab.ui.screens.SolicitarScreen
import com.ctma.prestamolab.ui.screens.SolicitudDetalleScreen
import com.ctma.prestamolab.ui.screens.TrazabilidadScreen
import com.ctma.prestamolab.ui.viewmodel.AuthViewModel
import com.ctma.prestamolab.ui.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestamoLabApp(
    viewModel: PrestamoViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.mensaje, authState.mensaje) {
        val mensaje = uiState.mensaje ?: authState.mensaje
        if (mensaje != null) {
            snackbarHostState.showSnackbar(mensaje)
            viewModel.limpiarMensaje()
            authViewModel.limpiarMensaje()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (authState.usuario == null) {
            LoginScreen(
                cargando = authState.cargando,
                onLogin = { correo, pass -> authViewModel.iniciarSesion(correo, pass) },
            )
        } else {
            val esAdmin = authState.usuario?.esAdministrador ?: false
            NavHost(
                navController = navController,
                startDestination = Rutas.CATALOGO,
                modifier = Modifier.padding(padding)
            ) {
                composable(Rutas.CATALOGO) {
                    CatalogoScreen(
                        equipos = uiState.equiposFiltrados,
                        busqueda = uiState.busqueda,
                        categoriaSeleccionada = uiState.categoriaSeleccionada,
                        esAdministrador = esAdmin,
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

                composable(Rutas.INVENTARIO) {
                    InventarioScreen(
                        onAgregarEquipo = { viewModel.agregarEquipo(it) { navController.popBackStack() } },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Rutas.GESTION_ADMIN) {
                    GestionSolicitudesAdminScreen(
                        solicitudes = uiState.solicitudes,
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
                    route = Rutas.TRAZABILIDAD,
                    arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
                ) { entry ->
                    val equipoId = entry.arguments?.getInt("equipoId") ?: -1
                    LaunchedEffect(equipoId) { viewModel.cargarTrazabilidad(equipoId) }
                    TrazabilidadScreen(
                        equipo = viewModel.obtenerEquipo(equipoId),
                        historial = uiState.trazabilidad,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Rutas.PERFIL) {
                    PerfilScreen(
                        usuario = authState.usuario,
                        onBack = { navController.popBackStack() },
                        onActualizar = { tel, correo -> authViewModel.actualizarContacto(tel, correo) },
                        onCerrarSesion = { authViewModel.cerrarSesion() },
                    )
                }

                composable(
                    route = Rutas.EQUIPO_DETALLE,
                    arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
                ) { entry ->
                    val equipoId = entry.arguments?.getInt("equipoId") ?: -1
                    EquipoDetalleScreen(
                        equipo = viewModel.obtenerEquipo(equipoId),
                        esAdministrador = esAdmin,
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
                            viewModel.crearSolicitud(equipoId, ambiente, proposito, duracion) { solicitudId ->
                                navController.navigate(Rutas.solicitudDetalle(solicitudId)) {
                                    popUpTo(Rutas.CATALOGO)
                                }
                            }
                        }
                    )
                }

                composable(Rutas.MIS_SOLICITUDES) {
                    MisSolicitudesScreen(
                        solicitudes = uiState.solicitudes,
                        equipos = uiState.equipos,
                        onBack = { navController.popBackStack() },
                        onSolicitudClick = { navController.navigate(Rutas.solicitudDetalle(it)) }
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
                        onBack = { navController.popBackStack() },
                        onCancelar = { viewModel.cancelarSolicitud(solicitudId) }
                    )
                }
            }
        }
    }
}
