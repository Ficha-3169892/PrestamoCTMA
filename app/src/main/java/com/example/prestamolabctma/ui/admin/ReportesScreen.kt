package com.example.prestamolabctma.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolabctma.model.ReporteEstadistico

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen(
    reporte: ReporteEstadistico?,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas del Laboratorio") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (reporte == null) {
                CircularProgressIndicator()
            } else {
                MetricCard(title = "Equipo Más Solicitado", value = reporte.equipoMasSolicitado)
                MetricCard(title = "Préstamos Totales", value = reporte.prestamosTotales.toString())
                MetricCard(title = "Horas Pico de Demanda", value = reporte.horasPico)
                
                Text(text = "Uso por Categoría:", style = MaterialTheme.typography.titleMedium)
                reporte.usoPorCategoria.forEach { (cat, count) ->
                    TechnicalRow(label = cat.name, value = "$count solicitudes")
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(text = value, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun TechnicalRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}
