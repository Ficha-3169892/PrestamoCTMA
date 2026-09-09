package com.ctma.prestamoctma.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ctma.prestamoctma.ui.screens.CatalogoScreen
import com.ctma.prestamoctma.ui.screens.MisSolicitudesScreen
import com.ctma.prestamoctma.ui.screens.SolicitarScreen
import com.ctma.prestamoctma.ui.viewmodel.PrestamoViewModel

sealed class Screen(val route: String) {
    data object Catalogo : Screen("catalogo")
    data object Solicitar : Screen("solicitar/{equipoId}") {
        fun createRoute(equipoId: String) = "solicitar/$equipoId"
    }
    data object MisSolicitudes : Screen("mis_solicitudes")
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    viewModel: PrestamoViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSolicitudExitosa) {
        if (uiState.isSolicitudExitosa) {
            navController.navigate(Screen.MisSolicitudes.route) {
                popUpTo(Screen.Catalogo.route)
            }
            viewModel.resetSolicitudExitosa()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Catalogo.route,
        modifier = modifier
    ) {
        composable(Screen.Catalogo.route) {
            CatalogoScreen(
                equipos = uiState.equipos,
                solicitudes = uiState.solicitudes,
                error = uiState.error,
                onEquipoClick = { equipo ->
                    navController.navigate(Screen.Solicitar.createRoute(equipo.id))
                },
                onVerSolicitudes = {
                    navController.navigate(Screen.MisSolicitudes.route)
                },
                onDismissError = { viewModel.clearError() }
            )
        }
        
        composable(
            route = Screen.Solicitar.route,
            arguments = listOf(navArgument("equipoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getString("equipoId") ?: ""
            SolicitarScreen(
                equipoId = equipoId,
                equipos = uiState.equipos,
                error = uiState.error,
                onSolicitar = { id, amb, prop, dur ->
                    viewModel.solicitarPrestamo(id, amb, prop, dur)
                },
                onBack = { 
                    viewModel.clearError()
                    navController.popBackStack() 
                },
                onDismissError = { viewModel.clearError() }
            )
        }

        composable(Screen.MisSolicitudes.route) {
            MisSolicitudesScreen(
                solicitudes = uiState.solicitudes,
                onCancelar = { viewModel.cancelarSolicitud(it) },
                onReportarDevolucion = { id, detalle, gravedad ->
                    viewModel.reportarNovedadDevolucion(id, detalle, gravedad)
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
