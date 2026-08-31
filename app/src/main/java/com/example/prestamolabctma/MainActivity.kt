package com.example.prestamolabctma

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prestamolabctma.data.repository.InMemoryPrestamoRepository
import com.example.prestamolabctma.model.RolUsuario
import com.example.prestamolabctma.navigation.Screen
import com.example.prestamolabctma.ui.admin.*
import com.example.prestamolabctma.ui.auth.LoginScreen
import com.example.prestamolabctma.ui.catalogo.CatalogoScreen
import com.example.prestamolabctma.ui.equipo.EquipoDetalleScreen
import com.example.prestamolabctma.ui.misprestamos.MisPrestamosScreen
import com.example.prestamolabctma.ui.profile.ProfileScreen
import com.example.prestamolabctma.ui.solicitud.SolicitudFormScreen
import com.example.prestamolabctma.ui.theme.PrestamoLabCTMATheme
import com.example.prestamolabctma.viewmodel.PrestamoViewModel

class MainActivity : ComponentActivity() {
    
    private val repository = InMemoryPrestamoRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            PrestamoLabCTMATheme {
                val viewModel: PrestamoViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return PrestamoViewModel(repository) as T
                        }
                    },
                )
                MainApp(viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: PrestamoViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val equiposFiltrados by viewModel.equiposFiltrados.collectAsState()
    val context = LocalContext.current
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Efecto para Toast de mensajes generales
    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.limpiarMensaje()
        }
    }

    // Efecto para Alertas de Vencimiento
    LaunchedEffect(uiState.solicitudesPorVencer) {
        if (uiState.solicitudesPorVencer.isNotEmpty()) {
            Toast.makeText(context, "ATENCIÓN: Tienes préstamos por vencer", Toast.LENGTH_LONG).show()
        }
    }

    // Navegación Automática por Estado de Sesión
    LaunchedEffect(uiState.usuarioLogueado, currentRoute) {
        val user = uiState.usuarioLogueado
        if (user == null) {
            if (currentRoute != Screen.Login.route && currentRoute != null) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        } else if (currentRoute == Screen.Login.route) {
            val startRoute = if (user.rol == RolUsuario.ADMINISTRADOR) Screen.AdminDashboard.route else Screen.Catalogo.route
            navController.navigate(startRoute) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            if (uiState.solicitudesVencidas.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "⚠️ TIENES PRÉSTAMOS VENCIDOS. Realice la devolución.",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLogin = { email -> viewModel.login(email) },
                    error = uiState.errorFormulario,
                    isLoading = uiState.guardando,
                )
            }

            composable(Screen.Catalogo.route) {
                val userRole = uiState.usuarioLogueado?.rol ?: RolUsuario.APRENDIZ
                val equiposAMostrar = if (userRole == RolUsuario.APRENDIZ) {
                    equiposFiltrados.filter { (it.estado != com.example.prestamolabctma.model.EstadoEquipo.MANTENIMIENTO && it.estado != com.example.prestamolabctma.model.EstadoEquipo.FUERA_DE_SERVICIO) }
                } else {
                    equiposFiltrados
                }

                CatalogoScreen(
                    equipos = equiposAMostrar,
                    searchQuery = uiState.searchQuery,
                    categoriaSeleccionada = uiState.categoriaSeleccionada,
                    onEquipoClick = { id -> navController.navigate(Screen.EquipoDetalle.createRoute(id)) },
                    onVerMisSolicitudes = { navController.navigate(Screen.MisPrestamos.route) },
                    onBusquedaChange = { q -> viewModel.actualizarBusqueda(q) },
                    onCategoriaSelect = { c -> viewModel.seleccionarCategoria(c) },
                    onToggleFavorito = { id -> viewModel.toggleFavorito(id) },
                    onVerPerfil = { navController.navigate(Screen.Perfil.route) },
                )
            }

            composable(
                route = Screen.EquipoDetalle.route,
                arguments = listOf(navArgument("equipoId") { type = NavType.IntType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("equipoId") ?: -1
                val equipo = uiState.equipos.find { it.id == id }
                EquipoDetalleScreen(
                    equipo = equipo,
                    onBack = { navController.popBackStack() }
                ) { equipoId -> 
                    navController.navigate(Screen.SolicitudForm.createRoute(equipoId)) 
                }
            }

            composable(
                route = Screen.SolicitudForm.route,
                arguments = listOf(navArgument("equipoId") { type = NavType.IntType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("equipoId") ?: -1
                val equipo = uiState.equipos.find { it.id == id }
                
                SolicitudFormScreen(
                    equipoNombre = equipo?.nombre ?: "Equipo",
                    onRegistrar = { a, p, d -> viewModel.registrarSolicitud(id, a, p, d) },
                    onBack = { navController.popBackStack() },
                    error = uiState.errorFormulario,
                    guardando = uiState.guardando,
                )
                
                LaunchedEffect(uiState.mensaje) {
                    if (uiState.mensaje == "Solicitud registrada con éxito") {
                        navController.navigate(Screen.Catalogo.route) {
                            popUpTo(Screen.Catalogo.route) { inclusive = true }
                        }
                    }
                }
            }

            composable(Screen.MisPrestamos.route) {
                MisPrestamosScreen(
                    solicitudes = uiState.solicitudes,
                    onBack = { navController.popBackStack() },
                    onCancelar = { solId -> viewModel.cancelarPrestamo(solId) },
                )
            }

            composable(Screen.Perfil.route) {
                ProfileScreen(
                    usuario = uiState.usuarioLogueado,
                    onBack = { navController.popBackStack() },
                    onActualizar = { tel, email -> viewModel.actualizarPerfil(tel, email) },
                    onLogout = { viewModel.logout() },
                )
            }

            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onLogout = { viewModel.logout() },
                    onVerPerfil = { navController.navigate(Screen.Perfil.route) },
                )
            }

            composable(Screen.RegistroEquipo.route) {
                RegistroEquipoScreen(
                    onRegistrar = { n, s, c, u, m, sp, a, url ->
                        viewModel.registrarNuevoEquipo(n, s, c, u, m, sp, a, url)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() },
                    error = uiState.errorFormulario,
                )
            }

            composable(Screen.GestionSolicitudes.route) {
                GestionSolicitudesScreen(
                    solicitudes = uiState.solicitudes,
                    onAprobar = { id -> viewModel.aprobarSolicitud(id) },
                    onRechazar = { id, j -> viewModel.rechazarSolicitud(id, j) },
                    onRecibir = { id, n, g -> viewModel.registrarRetorno(id, n, g) },
                    onBack = { navController.popBackStack() },
                )
            }

            composable(Screen.Reportes.route) {
                LaunchedEffect(Unit) { viewModel.cargarEstadisticas() }
                ReportesScreen(
                    reporte = uiState.estadisticas,
                    onBack = { navController.popBackStack() },
                )
            }

            composable("catalogo_admin") {
                CatalogoScreen(
                    equipos = uiState.equipos,
                    searchQuery = uiState.searchQuery,
                    categoriaSeleccionada = uiState.categoriaSeleccionada,
                    onEquipoClick = { id -> navController.navigate(Screen.Trazabilidad.createRoute(id)) },
                    onVerMisSolicitudes = { navController.navigate(Screen.MisPrestamos.route) },
                    onBusquedaChange = { q -> viewModel.actualizarBusqueda(q) },
                    onCategoriaSelect = { c -> viewModel.seleccionarCategoria(c) },
                    onToggleFavorito = { id -> viewModel.toggleFavorito(id) },
                    onVerPerfil = { navController.navigate(Screen.Perfil.route) },
                    onBack = { navController.popBackStack() },
                )
            }

            composable(
                route = Screen.Trazabilidad.route,
                arguments = listOf(navArgument("equipoId") { type = NavType.IntType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("equipoId") ?: -1
                val equipo = uiState.equipos.find { it.id == id }
                LaunchedEffect(id) { viewModel.cargarTrazabilidad(id) }
                TrazabilidadScreen(
                    equipoNombre = equipo?.nombre ?: "Equipo",
                    historial = uiState.trazabilidadEquipo,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
