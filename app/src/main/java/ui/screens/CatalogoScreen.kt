package com.ctma.prestamolab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    equipos: List<Equipo>,
    onSolicitarClick: (Int) -> Unit,
    onVerSolicitudesClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PréstamoLab CTMA - Catálogo") },
                actions = {
                    TextButton(onClick = onVerSolicitudesClick) {
                        Text("Mis Solicitudes")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(equipos) { equipo ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = equipo.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(text = "Categoría: ${equipo.categoria}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Estado: ${equipo.estado}", style = MaterialTheme.typography.labelMedium)
                        }
                        Button(
                            onClick = { onSolicitarClick(equipo.id) },
                            enabled = equipo.estado == EstadoEquipo.DISPONIBLE
                        ) {
                            Text("Solicitar")
                        }
                    }
                }
            }
        }
    }
}