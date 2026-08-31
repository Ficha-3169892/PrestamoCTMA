package com.ctma.prestamolab.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.EstadoEquipo

@Composable
fun AppHeader(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            onBack?.let {
                TextButton(onClick = it) {
                    Text("Volver")
                }
            }
            Text(title, style = MaterialTheme.typography.headlineSmall)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            }
        }
        if ((actionText != null) && (onAction != null)) {
            Button(onClick = onAction) {
                Text(actionText)
            }
        }
    }
}

@Composable
fun EstadoEquipoChip(estado: EstadoEquipo) {
    val label = when (estado) {
        EstadoEquipo.DISPONIBLE -> "Disponible"
        EstadoEquipo.RESERVADO -> "Reservado"
        EstadoEquipo.PRESTADO -> "Prestado"
        EstadoEquipo.MANTENIMIENTO -> "Mantenimiento"
        EstadoEquipo.FUERA_DE_SERVICIO -> "Fuera de servicio"
    }
    AssistChip(onClick = {}, label = { Text(label) })
}

@Composable
fun EmptyState(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
