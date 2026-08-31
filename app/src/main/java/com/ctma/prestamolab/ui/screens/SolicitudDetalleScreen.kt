package com.ctma.prestamolab.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoSolicitud
import com.ctma.prestamolab.model.SolicitudPrestamo
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay

@Composable
fun SolicitudDetalleScreen(
    solicitud: SolicitudPrestamo?,
    equipo: Equipo?,
    onBack: () -> Unit,
    onCancelar: () -> Unit,
) {
    var mostrarAlertaTiempo by remember { mutableStateOf(value = false) }

    // Simulación de alerta de 15 minutos (HU-14)
    LaunchedEffect(solicitud?.estado) {
        if ((solicitud?.estado == EstadoSolicitud.APROBADA) || (solicitud?.estado == EstadoSolicitud.ENTREGADA)) {
            delay(5.seconds) // Simular que el tiempo pasa
            mostrarAlertaTiempo = true
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Detalle de solicitud", onBack = onBack)
        if (solicitud == null) {
            EmptyState("La solicitud indicada no existe. La app conserva un estado recuperable.")
            return@Column
        }

        if (mostrarAlertaTiempo) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            ) {
                Text(
                    "¡ATENCIÓN! Faltan menos de 15 minutos para la entrega del equipo.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
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
                Text("Solicitud ${solicitud.id}", style = MaterialTheme.typography.headlineSmall)
                Text("Equipo: ${equipo?.nombre ?: "Equipo no encontrado"}")
                Text("Estado: ${solicitud.estado}")
                
                if ((solicitud.estado == EstadoSolicitud.RECHAZADA) && (solicitud.justificacionRechazo != null)) {
                    Text("Justificación rechazo: ${solicitud.justificacionRechazo}", color = Color.Red)
                }

                if (solicitud.estado == EstadoSolicitud.DEVUELTA && solicitud.novedades != null) {
                    Text("Novedades registradas: ${solicitud.novedades}")
                }

                Text("Ambiente o destino: ${solicitud.ambienteDestino}")
                Text("Propósito: ${solicitud.proposito}")
                Text("Duración estimada: ${solicitud.duracionHoras} horas")
                Button(
                    onClick = onCancelar,
                    enabled = solicitud.estado == EstadoSolicitud.SOLICITADA,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (solicitud.estado == EstadoSolicitud.SOLICITADA) {
                            "Cancelar solicitud"
                        } else {
                            "Cancelación no disponible"
                        }
                    )
                }
            }
        }
    }
}
