package com.example.prestamolabctma.ui.equipo

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.EstadoEquipo
import com.example.prestamolabctma.ui.catalogo.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoDetalleScreen(
    equipo: Equipo?,
    onBack: () -> Unit,
    onSolicitar: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Equipo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
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
            if (equipo == null) {
                Text("Error: El equipo solicitado no existe.", color = MaterialTheme.colorScheme.error)
            } else {
                Text(text = equipo.nombre, style = MaterialTheme.typography.headlineMedium)
                Text(text = "Categoría: ${equipo.categoria}", style = MaterialTheme.typography.titleMedium)
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Estado actual: ")
                    StatusBadge(estado = equipo.estado)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onSolicitar(equipo.id) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = equipo.estado == EstadoEquipo.DISPONIBLE
                ) {
                    Text("Solicitar Préstamo")
                }
                
                if (equipo.estado != EstadoEquipo.DISPONIBLE) {
                    Text(
                        text = "Este equipo no se encuentra disponible para préstamo en este momento.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
