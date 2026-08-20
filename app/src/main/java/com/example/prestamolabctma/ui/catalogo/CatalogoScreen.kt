package com.example.prestamolabctma.ui.catalogo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.EstadoEquipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    equipos: List<Equipo>,
    onEquipoClick: (Int) -> Unit,
    onVerMisSolicitudes: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PréstamoLab - Catálogo") },
                actions = {
                    Button(onClick = onVerMisSolicitudes) {
                        Text("Mis Solicitudes")
                    }
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(equipos) { equipo ->
                EquipoItem(equipo = equipo) { onEquipoClick(equipo.id) }
            }
        }
    }
}

@Composable
fun EquipoItem(equipo: Equipo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = equipo.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = "Categoría: ${equipo.categoria}", style = MaterialTheme.typography.bodySmall)
            }
            
            StatusBadge(estado = equipo.estado)
        }
    }
}

@Composable
fun StatusBadge(estado: EstadoEquipo) {
    val color = when (estado) {
        EstadoEquipo.DISPONIBLE -> MaterialTheme.colorScheme.primary
        EstadoEquipo.RESERVADO -> MaterialTheme.colorScheme.secondary
        EstadoEquipo.PRESTADO -> MaterialTheme.colorScheme.error
    }
    
    Surface(
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = estado.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
