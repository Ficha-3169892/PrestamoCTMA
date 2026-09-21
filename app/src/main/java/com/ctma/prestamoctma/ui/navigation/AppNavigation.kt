package com.ctma.prestamoctma.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ctma.prestamoctma.ui.screens.AgregarEquipoScreen
import com.ctma.prestamoctma.ui.screens.CatalogoScreen
import com.ctma.prestamoctma.ui.screens.MisSolicitudesScreen
import com.ctma.prestamoctma.ui.screens.SolicitarScreen
import com.ctma.prestamoctma.ui.viewmodel.ListadoUiState
import com.ctma.prestamoctma.ui.viewmodel.OperacionUiState
import com.ctma.prestamoctma.ui.viewmodel.PrestamoViewModel

sealed class Screen(val route: String) {
    data object Catalogo : Screen("catalogo")
    data object Solicitar : Screen("solicitar/{equipoId}") {
        fun createRoute(equipoId: String) = "solicitar/$equipoId"
    }
    data object MisSolicitudes : Screen("mis_solicitudes")
    data object AgregarEquipo : Screen("agregar_equipo")
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    viewModel: PrestamoViewModel = viewModel(factory = PrestamoViewModel.Factory)
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.operacionEstado) {
        if (uiState.operacionEstado == OperacionUiState.Exito) {
            // Si estábamos agregando o solicitando, navegamos o retrocedemos
            // Aquí hay que ser cuidadoso con la ruta actual
            val currentRoute = navController.currentDestination?.route
            if (currentRoute?.startsWith("solicitar") == true) {
                navController.navigate(Screen.MisSolicitudes.route) {
                    popUpTo(Screen.Catalogo.route)
                }
            } else if (currentRoute == Screen.AgregarEquipo.route) {
                navController.popBackStack()
            }
            viewModel.resetOperacionEstado()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Catalogo.route,
        modifier = modifier
    ) {
        composable(Screen.Catalogo.route) {
            val solicitudes = (uiState.listadoSolicitudes as? ListadoUiState.Contenido)?.items ?: emptyList()

            CatalogoScreen(
                listadoEquipos = uiState.listadoEquipos,
                solicitudes = solicitudes,
                searchQuery = uiState.searchQuery,
                isRefreshing = uiState.isRefreshing,
                error = uiState.errorMensaje,
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                onEquipoClick = { equipo ->
                    navController.navigate(Screen.Solicitar.createRoute(equipo.id))
                },
                onVerSolicitudes = {
                    navController.navigate(Screen.MisSolicitudes.route)
                },
                onAgregarEquipo = {
                    navController.navigate(Screen.AgregarEquipo.route)
                },
                onDismissError = { viewModel.clearError() }
            )
        }
        
        composable(Screen.AgregarEquipo.route) {
            AgregarEquipoScreen(
                onAgregar = { nombre, cat, desc ->
                    viewModel.agregarEquipo(nombre, cat, desc)
                },
                onBack = { 
                    viewModel.clearError()
                    navController.popBackStack() 
                },
                error = uiState.errorMensaje,
                operacionEstado = uiState.operacionEstado,
                onDismissError = { viewModel.clearError() }
            )
        }
        
        composable(
            route = Screen.Solicitar.route,
            arguments = listOf(navArgument("equipoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getString("equipoId") ?: ""
            val equipos = (uiState.listadoEquipos as? ListadoUiState.Contenido)?.items ?: emptyList()
            
            SolicitarScreen(
                equipoId = equipoId,
                equipos = equipos,
                error = uiState.errorMensaje,
                operacionEstado = uiState.operacionEstado,
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
                listadoSolicitudes = uiState.listadoSolicitudes,
                evidencias = uiState.evidenciasPorPrestamo,
                onCancelar = { viewModel.cancelarSolicitud(it) },
                onReportarDevolucion = { id, detalle, gravedad ->
                    viewModel.reportarNovedadDevolucion(id, detalle, gravedad)
                },
                onGuardarEvidencia = { id, uri, size, type ->
                    viewModel.guardarEvidencia(id, uri, size, type)
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
