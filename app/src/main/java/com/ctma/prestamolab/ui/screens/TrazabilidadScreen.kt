package com.ctma.prestamolab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo
import com.ctma.prestamolab.ui.state.ListadoUiState
import java.text.SimpleDateFormat
import java.util.*

/**
 * [HU 16] Trazabilidad Histórica por Equipo.
 * [HU 13] Reporte de Novedades (S6-S9).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrazabilidadScreen(
    equipo: Equipo?,
    historialState: ListadoUiState<SolicitudPrestamo>,
    onBack: () -> Unit,
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trazabilidad de Equipo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (equipo == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Text(equipo.nombre, style = MaterialTheme.typography.headlineSmall)
                Text("Serie: ${equipo.serie}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Historial de Movimientos", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                when (historialState) {
                    is ListadoUiState.Cargando -> CircularProgressIndicator()
                    is ListadoUiState.Vacio -> Text("Este equipo no tiene historial de préstamos.")
                    is ListadoUiState.Error -> Text("Error: ${historialState.mensaje}")
                    is ListadoUiState.Contenido -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(historialState.datos) { sol ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(
                                            "Fecha: ${dateFormat.format(Date(sol.fechaCreacion))}",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                        Text("Estado Final: ${sol.estado}")
                                        Text("Propósito: ${sol.proposito}")
                                        if (!sol.novedades.isNullOrBlank()) {
                                            Text("Novedades: ${sol.novedades}", color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
