package com.example.prestamolabctma.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolabctma.model.SolicitudPrestamo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrazabilidadScreen(
    equipoNombre: String,
    historial: List<SolicitudPrestamo>,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trazabilidad: $equipoNombre") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (historial.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No hay historial de préstamos para este equipo.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(historial) { sol ->
                    TrazabilidadItem(sol = sol)
                }
            }
        }
    }
}

@Composable
fun TrazabilidadItem(sol: SolicitudPrestamo) {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val fechaStr = sdf.format(Date(sol.fechaSolicitud))

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Fecha: $fechaStr", style = MaterialTheme.typography.labelSmall)
            Text(text = "Propósito: ${sol.proposito}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Estado Final: ${sol.estado}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            
            if (sol.novedadDevolucion != null) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(text = "Novedad registrada:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                Text(text = sol.novedadDevolucion, style = MaterialTheme.typography.bodySmall)
                Text(text = "Gravedad: ${sol.gravedadNovedad}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
