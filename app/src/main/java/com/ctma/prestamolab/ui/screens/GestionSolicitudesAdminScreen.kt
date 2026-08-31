package com.ctma.prestamolab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoSolicitud
import com.ctma.prestamolab.model.SolicitudPrestamo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionSolicitudesAdminScreen(
    solicitudes: List<SolicitudPrestamo>,
    equipos: List<Equipo>,
    onAprobar: (Int) -> Unit,
    onRechazar: (Int, String) -> Unit,
    onDevolver: (Int, String?) -> Unit,
    onBack: () -> Unit,
) {
    var solicitudAAccion by remember { mutableStateOf<SolicitudPrestamo?>(null) }
    var mostrarDialogoRechazo by remember { mutableStateOf(value = false) }
    var mostrarDialogoDevolucion by remember { mutableStateOf(value = false) }
    var justificacion by remember { mutableStateOf("") }
    var novedades by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Préstamos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        }
    ) { padding ->
        if (solicitudes.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay solicitudes para gestionar.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(solicitudes) { sol ->
                    val equipo = equipos.firstOrNull { it.id == sol.equipoId }
                    SolicitudAdminCard(
                        solicitud = sol,
                        equipoNombre = equipo?.nombre ?: "Desconocido",
                        onAprobar = { onAprobar(sol.id) },
                        onRechazar = {
                            solicitudAAccion = sol
                            mostrarDialogoRechazo = true
                        }
                    ) {
                        solicitudAAccion = sol
                        mostrarDialogoDevolucion = true
                    }
                }
            }
        }

        if (mostrarDialogoRechazo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoRechazo = false },
                title = { Text("Rechazar Solicitud") },
                text = {
                    OutlinedTextField(
                        value = justificacion,
                        onValueChange = { justificacion = it },
                        label = { Text("Motivo del rechazo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            solicitudAAccion?.let { onRechazar(it.id, justificacion) }
                            mostrarDialogoRechazo = false
                            justificacion = ""
                        },
                        enabled = justificacion.isNotBlank()
                    ) { Text("Rechazar") }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoRechazo = false }) { Text("Cancelar") }
                }
            )
        }

        if (mostrarDialogoDevolucion) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoDevolucion = false },
                title = { Text("Registrar Devolución") },
                text = {
                    Column {
                        Text("Reportar novedades o daños (opcional):")
                        OutlinedTextField(
                            value = novedades,
                            onValueChange = { novedades = it },
                            label = { Text("Novedades") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            solicitudAAccion?.let { onDevolver(it.id, novedades.ifBlank { null }) }
                            mostrarDialogoDevolucion = false
                            novedades = ""
                        }
                    ) { Text("Completar Devolución") }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoDevolucion = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
fun SolicitudAdminCard(
    solicitud: SolicitudPrestamo,
    equipoNombre: String,
    onAprobar: () -> Unit,
    onRechazar: () -> Unit,
    onDevolver: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Solicitud #${solicitud.id}", style = MaterialTheme.typography.titleMedium)
            Text("Equipo: $equipoNombre")
            Text("Ambiente: ${solicitud.ambienteDestino}")
            Text("Estado: ${solicitud.estado}")
            
            if (solicitud.estado == EstadoSolicitud.SOLICITADA) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onRechazar, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                        Text("Rechazar")
                    }
                    Button(onClick = onAprobar) {
                        Text("Aprobar")
                    }
                }
            } else if ((solicitud.estado == EstadoSolicitud.APROBADA) || (solicitud.estado == EstadoSolicitud.ENTREGADA)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = onDevolver) {
                        Text("Registrar Devolución")
                    }
                }
            }
        }
    }
}
