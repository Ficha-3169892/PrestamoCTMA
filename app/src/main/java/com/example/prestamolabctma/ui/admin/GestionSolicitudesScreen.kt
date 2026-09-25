package com.example.prestamolabctma.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolabctma.model.EstadoSolicitud
import com.example.prestamolabctma.model.NivelGravedad
import com.example.prestamolabctma.model.SolicitudPrestamo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionSolicitudesScreen(
    solicitudes: List<SolicitudPrestamo>,
    onAprobar: (Int) -> Unit,
    onRechazar: (Int, String) -> Unit,
    onRecibir: (Int, String?, NivelGravedad?) -> Unit,
    onBack: () -> Unit,
) {
    var solicitudAgestionar by remember { mutableStateOf<SolicitudPrestamo?>(null) }
    var justificacion by remember { mutableStateOf("") }
    var novedad by remember { mutableStateOf("") }
    var gravedad by remember { mutableStateOf(value = NivelGravedad.LEVE) }
    var mostrarDialogoRechazo by remember { mutableStateOf(value = false) }
    var mostrarDialogoRecibir by remember { mutableStateOf(value = false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Solicitudes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(solicitudes.filter { (it.estado != EstadoSolicitud.CANCELADA && it.estado != EstadoSolicitud.COMPLETADA) }) { sol ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Solicitud #${sol.id} - Equipo ID: ${sol.equipoId}", style = MaterialTheme.typography.titleMedium)
                        Text(text = "Estado: ${sol.estado}")
                        Text(text = "Ambiente: ${sol.ambienteDestino}")
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            if (sol.estado == EstadoSolicitud.SOLICITADA) {
                                Button(onClick = { onAprobar(sol.id) }) { Text("Aprobar") }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        solicitudAgestionar = sol
                                        mostrarDialogoRechazo = true
                                    },
                                ) { Text("Rechazar", color = MaterialTheme.colorScheme.error) }
                            } else if (sol.estado == EstadoSolicitud.APROBADA || sol.estado == EstadoSolicitud.ENTREGADA) {
                                Button(onClick = {
                                    solicitudAgestionar = sol
                                    mostrarDialogoRecibir = true
                                }) { Text("Recibir Equipo") }
                            }
                        }
                    }
                }
            }
        }

        if (mostrarDialogoRechazo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoRechazo = false },
                title = { Text("Justificar Rechazo") },
                text = {
                    OutlinedTextField(
                        value = justificacion,
                        onValueChange = { justificacion = it },
                        label = { Text("Motivo del rechazo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            solicitudAgestionar?.let { onRechazar(it.id, justificacion) }
                            mostrarDialogoRechazo = false
                            justificacion = ""
                        },
                        enabled = justificacion.isNotBlank()
                    ) { Text("Rechazar") }
                }
            )
        }

        if (mostrarDialogoRecibir) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoRecibir = false },
                title = { Text("Reportar Novedades al Recibir") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = novedad,
                            onValueChange = { novedad = it },
                            label = { Text("Observaciones / Daños") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Gravedad:")
                        Row {
                            NivelGravedad.entries.forEach { n ->
                                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                    RadioButton(selected = gravedad == n, onClick = { gravedad = n })
                                    Text(text = n.name)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            solicitudAgestionar?.let { onRecibir(it.id, novedad.ifBlank { null }, gravedad) }
                            mostrarDialogoRecibir = false
                            novedad = ""
                        },
                    ) { Text("Confirmar Recepción") }
                }
            )
        }
    }
}
