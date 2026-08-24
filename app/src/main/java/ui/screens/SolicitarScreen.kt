package com.ctma.prestamolab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.Equipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitarScreen(
    equipo: Equipo?,
    mensajeError: String?,
    mensajeExito: String?,
    guardando: Boolean,
    onGuardar: (ambiente: String, proposito: String, duracion: Int) -> Unit,
    onVolver: () -> Unit
) {
    var ambiente by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracionTexto by remember { mutableStateOf("1") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar Préstamo") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            equipo?.let {
                Text(text = "Equipo: ${it.nombre}", style = MaterialTheme.typography.titleLarge)
            }

            OutlinedTextField(
                value = ambiente,
                onValueChange = { ambiente = it },
                label = { Text("Ambiente de destino (Ej: Lab 302)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = proposito,
                onValueChange = { proposito = it },
                label = { Text("Propósito de uso (10 a 180 caracteres)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = duracionTexto,
                onValueChange = { duracionTexto = it },
                label = { Text("Duración en horas (1 a 8)") },
                modifier = Modifier.fillMaxWidth()
            )

            mensajeError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            mensajeExito?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary)
            }

            Button(
                onClick = {
                    val duracion = duracionTexto.toIntOrNull() ?: 0
                    onGuardar(ambiente, proposito, duracion)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !guardando
            ) {
                if (guardando) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Enviar Solicitud")
                }
            }
        }
    }
}