package com.example.prestamolabctma

import android.os.Bundle
<<<<<<< HEAD
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prestamolabctma.data.repository.InMemoryPrestamoRepository
import com.example.prestamolabctma.navigation.Screen
import com.example.prestamolabctma.ui.catalogo.CatalogoScreen
import com.example.prestamolabctma.ui.equipo.EquipoDetalleScreen
import com.example.prestamolabctma.ui.misprestamos.MisPrestamosScreen
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
                    }
                )
                MainApp(viewModel)
=======
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.prestamolabctma.ui.theme.PrestamoLabCTMATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrestamoLabCTMATheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
>>>>>>> origin/dev
            }
        }
    }
}

@Composable
<<<<<<< HEAD
fun MainApp(viewModel: PrestamoViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.limpiarMensaje()
        }
    }

    NavHost(navController = navController, startDestination = Screen.Catalogo.route) {
        composable(Screen.Catalogo.route) {
            CatalogoScreen(
                equipos = uiState.equipos,
                onEquipoClick = { id -> 
                    navController.navigate(Screen.EquipoDetalle.createRoute(id)) 
                },
            ) {
                navController.navigate(Screen.MisPrestamos.route)
            }
        }

        composable(
            route = Screen.EquipoDetalle.route,
            arguments = listOf(navArgument("equipoId") { type = NavType.IntType }),
        ) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getInt("equipoId") ?: -1
            val equipo = uiState.equipos.find { it.id == equipoId }
            
            EquipoDetalleScreen(
                equipo = equipo,
                onBack = { navController.popBackStack() },
                onSolicitar = { id ->
                    navController.navigate(Screen.SolicitudForm.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.SolicitudForm.route,
            arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getInt("equipoId") ?: -1
            val equipo = uiState.equipos.find { it.id == equipoId }
            
            SolicitudFormScreen(
                equipoNombre = equipo?.nombre ?: "Equipo Desconocido",
                onRegistrar = { ambiente, proposito, duracion ->
                    viewModel.registrarSolicitud(equipoId, ambiente, proposito, duracion)
                },
                onBack = { navController.popBackStack() },
                error = uiState.errorFormulario,
                guardando = uiState.guardando
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
                onCancelar = { id -> viewModel.cancelarPrestamo(id) }
            )
        }
    }
}
=======
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrestamoLabCTMATheme {
        Greeting("Android")
    }
}
>>>>>>> origin/dev
