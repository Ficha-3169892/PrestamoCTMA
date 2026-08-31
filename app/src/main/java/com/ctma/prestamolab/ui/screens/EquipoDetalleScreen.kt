package com.ctma.prestamolab.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo

@Composable
fun EquipoDetalleScreen(
    equipo: Equipo?,
    esAdministrador: Boolean,
    onBack: () -> Unit,
    onSolicitarClick: () -> Unit,
    onTrazabilidadClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Detalle del equipo", onBack = onBack)
        if (equipo == null) {
            EmptyState("El equipo solicitado no existe. Puedes volver al catálogo sin cierre abrupto.")
            return@Column
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(equipo.nombre, style = MaterialTheme.typography.headlineSmall)
                Text("Serie: ${equipo.serie}", style = MaterialTheme.typography.bodyLarge)
                Text("Marca: ${equipo.marca}", style = MaterialTheme.typography.bodyLarge)
                Text("Categoría: ${equipo.categoria}")
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Disponibilidad")
                    EstadoEquipoChip(equipo.estado)
                }

                Text("Ubicación de entrega: ${equipo.ubicacion}", style = MaterialTheme.typography.bodyLarge)

                Column {
                    Text("Especificaciones:", style = MaterialTheme.typography.titleSmall)
                    Text(equipo.especificaciones, style = MaterialTheme.typography.bodyMedium)
                }

                if (equipo.accesorios.isNotEmpty()) {
                    Column {
                        Text("Accesorios incluidos:", style = MaterialTheme.typography.titleSmall)
                        equipo.accesorios.forEach { accesorio ->
                            Text("- $accesorio", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Button(
                    onClick = onSolicitarClick,
                    enabled = equipo.estado == EstadoEquipo.DISPONIBLE,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (equipo.estado == EstadoEquipo.DISPONIBLE) "Solicitar préstamo" else "Equipo no disponible")
                }

                if (esAdministrador) {
                    Button(
                        onClick = onTrazabilidadClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Ver Trazabilidad")
                    }
                }
            }
        }
    }
}
