package com.ctma.prestamolab.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo

@Composable
fun MisSolicitudesScreen(
    solicitudes: List<SolicitudPrestamo>,
    equipos: List<Equipo>,
    onBack: () -> Unit,
    onSolicitudClick: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(
            title = "Mis solicitudes",
            subtitle = "Solicitudes registradas durante esta ejecución",
            onBack = onBack
        )

        if (solicitudes.isEmpty()) {
            EmptyState("Aún no hay solicitudes registradas.")
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(solicitudes, key = { it.id }) { solicitud ->
                val equipo = equipos.firstOrNull { it.id == solicitud.equipoId }
                SolicitudCard(
                    solicitud = solicitud,
                    equipoNombre = equipo?.nombre ?: "Equipo no encontrado",
                    onClick = { onSolicitudClick(solicitud.id) },
                )
            }
        }
    }
}

@Composable
private fun SolicitudCard(
    solicitud: SolicitudPrestamo,
    equipoNombre: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Solicitud ${solicitud.id}", style = MaterialTheme.typography.titleMedium)
                Text(equipoNombre, style = MaterialTheme.typography.bodyMedium)
                Text("Duración: ${solicitud.duracionHoras} horas")
            }
            Text(solicitud.estado.name, style = MaterialTheme.typography.labelLarge)
        }
    }
}
