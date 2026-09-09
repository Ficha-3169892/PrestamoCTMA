package com.ctma.prestamoctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ctma.prestamoctma.R
import com.ctma.prestamoctma.model.EstadoSolicitud
import com.ctma.prestamoctma.model.GravedadDano
import com.ctma.prestamoctma.model.SolicitudPrestamo
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisSolicitudesScreen(
    solicitudes: List<SolicitudPrestamo>,
    onCancelar: (String) -> Unit,
    onReportarDevolucion: (String, String, GravedadDano) -> Unit,
    onBack: () -> Unit
) {
    var solicitudParaReportar by remember { mutableStateOf<SolicitudPrestamo?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.mis_solicitudes_desc)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_desc)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Aviso persistente de solicitudes vencidas
            val vencidas = solicitudes.filter { it.estaVencida() }
            if (vencidas.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Tienes ${vencidas.size} solicitud(es) vencida(s). Por favor realiza la devolución.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (solicitudes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_solicitudes_msg))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(solicitudes) { solicitud ->
                        SolicitudCard(
                            solicitud = solicitud, 
                            onCancelar = { onCancelar(solicitud.id) },
                            onDevolver = { solicitudParaReportar = solicitud }
                        )
                    }
                }
            }
        }

        solicitudParaReportar?.let { solicitud ->
            DevolucionDialog(
                onDismiss = { solicitudParaReportar = null },
                onConfirm = { novedad, gravedad ->
                    onReportarDevolucion(solicitud.id, novedad, gravedad)
                    solicitudParaReportar = null
                }
            )
        }
    }
}

@Composable
fun SolicitudCard(
    solicitud: SolicitudPrestamo, 
    onCancelar: () -> Unit,
    onDevolver: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    
    val esVencida = solicitud.estaVencida()
    val faltaPoco = solicitud.faltaPocoParaVencer()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = if (esVencida) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
        } else if (faltaPoco) {
            CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)) // Amarillo suave
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (faltaPoco) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFBC02D), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "¡Atención! Vence en menos de 15 min",
                        color = Color(0xFFFBC02D),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = solicitud.nombreEquipo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StatusChip(estado = solicitud.estado)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Destino: ${solicitud.ambienteDestino}", style = MaterialTheme.typography.bodyMedium)
            Text(text = stringResource(R.string.desde_label, dateFormat.format(solicitud.fechaSolicitud)))
            Text(text = "Vence: ${dateFormat.format(solicitud.fechaVencimiento)}", style = MaterialTheme.typography.bodySmall)
            
            if (solicitud.novedadDetalle != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Novedad: ${solicitud.novedadDetalle}", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                Text(text = "Gravedad: ${solicitud.gravedadDano}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (solicitud.estado == EstadoSolicitud.SOLICITADA) {
                    OutlinedButton(
                        onClick = onCancelar,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(stringResource(R.string.cancelar_button))
                    }
                }
                
                if (solicitud.estado == EstadoSolicitud.SOLICITADA || solicitud.estado == EstadoSolicitud.ENTREGADA) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onDevolver) {
                        Text("Reportar Devolución")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevolucionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, GravedadDano) -> Unit
) {
    var novedad by remember { mutableStateOf("") }
    var gravedad by remember { mutableStateOf(GravedadDano.NINGUNA) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reportar Novedad de Devolución") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = novedad,
                    onValueChange = { novedad = it },
                    label = { Text("Detalle de la novedad/daño") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Gravedad: ${gravedad.name}")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        GravedadDano.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name) },
                                onClick = {
                                    gravedad = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(novedad, gravedad) }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun StatusChip(estado: EstadoSolicitud) {
    val color = when (estado) {
        EstadoSolicitud.SOLICITADA -> Color(0xFF2196F3)
        EstadoSolicitud.APROBADA -> Color(0xFF4CAF50)
        EstadoSolicitud.ENTREGADA -> Color(0xFF9C27B0)
        EstadoSolicitud.DEVUELTA -> Color(0xFF795548)
        EstadoSolicitud.CANCELADA -> Color.Gray
        EstadoSolicitud.RECHAZADA -> Color(0xFFF44336)
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small,
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = estado.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
