package com.ctma.prestamoctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ctma.prestamoctma.model.Equipo
import com.ctma.prestamoctma.ui.viewmodel.OperacionUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitarScreen(
    equipoId: String,
    equipos: List<Equipo>,
    error: String?,
    operacionEstado: OperacionUiState = OperacionUiState.Idle,
    onSolicitar: (String, String, String, Int) -> Unit,
    onBack: () -> Unit,
    onDismissError: () -> Unit
) {
    val equipo = equipos.find { it.id == equipoId }
    
    val isLoading = operacionEstado == OperacionUiState.Ejecutando
    
    var ambiente by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracionStr by remember { mutableStateOf("1") }

    LaunchedEffect(error) {
        if (error != null) {
            // Se podría mostrar un Snackbar aquí si se tuviera ScaffoldState
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Préstamo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        if (equipo == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Error: Equipo no encontrado")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = equipo.nombre, style = MaterialTheme.typography.headlineSmall)
                Text(text = "Categoría: ${equipo.categoria.name}", style = MaterialTheme.typography.bodyMedium)
                
                if (error != null) {
                    Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                OutlinedTextField(
                    value = ambiente,
                    onValueChange = { ambiente = it },
                    label = { Text("Ambiente de Destino (Obligatorio)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )

                OutlinedTextField(
                    value = proposito,
                    onValueChange = { proposito = it },
                    label = { Text("Propósito (10-180 caracteres)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    enabled = !isLoading
                )

                OutlinedTextField(
                    value = duracionStr,
                    onValueChange = { if (it.all { char -> char.isDigit() }) duracionStr = it },
                    label = { Text("Duración en horas (1-8)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { 
                        onSolicitar(equipo.id, ambiente, proposito, duracionStr.toIntOrNull() ?: 0) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Guardar Solicitud")
                    }
                }
            }
        }
    }
}
