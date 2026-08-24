package com.ctma.prestamolab.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ctma.prestamolab.ui.screens.CatalogoScreen
import com.ctma.prestamolab.ui.screens.MisSolicitudesScreen
import com.ctma.prestamolab.ui.screens.SolicitarScreen
import com.ctma.prestamolab.ui.viewmodel.PrestamoViewModel

@Composable
fun AppNavigation(
    viewModel: PrestamoViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "catalogo"
    ) {
        composable("catalogo") {
            CatalogoScreen(
                equipos = uiState.equipos,
                onSolicitarClick = { equipoId ->
                    viewModel.seleccionarEquipo(equipoId)
                    viewModel.limpiarMensajes()
                    navController.navigate("solicitar/$equipoId")
                },
                onVerSolicitudesClick = {
                    navController.navigate("mis_solicitudes")
                }
            )
        }

        composable(
            route = "solicitar/{equipoId}",
            arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getInt("equipoId") ?: 0

            SolicitarScreen(
                equipo = uiState.equipoSeleccionado,
                mensajeError = uiState.mensajeError,
                mensajeExito = uiState.mensajeExito,
                guardando = uiState.guardando,
                onGuardar = { ambiente, proposito, duracion ->
                    viewModel.registrarSolicitud(equipoId, ambiente, proposito, duracion)
                },
                onVolver = {
                    viewModel.limpiarMensajes()
                    navController.popBackStack()
                }
            )
        }

        composable("mis_solicitudes") {
            MisSolicitudesScreen(
                solicitudes = uiState.solicitudes,
                onCancelarClick = { solicitudId ->
                    viewModel.cancelarSolicitud(solicitudId)
                },
                onVolver = {
                    viewModel.limpiarMensajes()
                    navController.popBackStack()
                }
            )
        }
    }
}