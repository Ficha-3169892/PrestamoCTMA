package com.example.prestamolabctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prestamolabctma.ui.theme.PrestamoLabCTMATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicialización manual del repositorio para propósitos de demostración.
        // En una app real se usaría Inyección de Dependencias.
        val repository = InMemoryPrestamoRepository()
        
        enableEdgeToEdge()
        setContent {
            PrestamoLabCTMATheme {
                val viewModel: PrestamoViewModel = viewModel(
                    factory = PrestamoViewModelFactory(repository)
                )
                val uiState by viewModel.uiState.collectAsState()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CatalogoScreen(
                        equipos = uiState.equipos,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
