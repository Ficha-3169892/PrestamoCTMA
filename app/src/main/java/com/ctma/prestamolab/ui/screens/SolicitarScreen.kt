package com.ctma.prestamolab.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.domain.ErroresSolicitud
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo

@Composable
fun SolicitarScreen(
    equipo: Equipo?,
    errores: ErroresSolicitud,
    guardando: Boolean,
    onBack: () -> Unit,
    onGuardar: (String, String, String) -> Unit,
) {
    var ambiente by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Registrar solicitud", onBack = onBack)
        if (equipo == null) {
            EmptyState("No se encontró el equipo indicado por la navegación.")
            return@Column
        }
        if (equipo.estado != EstadoEquipo.DISPONIBLE) {
            EmptyState("Solo se puede solicitar un equipo DISPONIBLE. Estado actual: ${equipo.estado}.")
            return@Column
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(equipo.nombre, style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = ambiente,
                    onValueChange = { ambiente = it },
                    label = { Text("Ambiente o destino") },
                    supportingText = { errores.ambiente?.let { Text(it) } },
                    isError = errores.ambiente != null,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = proposito,
                    onValueChange = { proposito = it },
                    label = { Text("Propósito") },
                    supportingText = {
                        Text(errores.proposito ?: "${proposito.trim().length}/180 caracteres. Mínimo 10.")
                    },
                    isError = errores.proposito != null,
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = duracion,
                    onValueChange = { duracion = it.filter(Char::isDigit).take(1) },
                    label = { Text("Duración estimada en horas") },
                    supportingText = { errores.duracion?.let { Text(it) } },
                    isError = errores.duracion != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onGuardar(ambiente, proposito, duracion) },
                    enabled = !guardando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (guardando) "Guardando..." else "Guardar solicitud")
                }
            }
        }
    }
}
