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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoSolicitud
import com.ctma.prestamolab.model.SolicitudPrestamo

@Composable
fun SolicitudDetalleScreen(
    solicitud: SolicitudPrestamo?,
    equipo: Equipo?,
    alertaActiva: Boolean, // [HU-15] Recibido desde ViewModel para centralizar lógica
    onBack: () -> Unit,
    onCancelar: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Detalle de solicitud", onBack = onBack)
        if (solicitud == null) {
            EmptyState("La solicitud indicada no existe. La app conserva un estado recuperable.")
            return@Column
        }

        if (alertaActiva && (solicitud.estado == EstadoSolicitud.APROBADA || solicitud.estado == EstadoSolicitud.ENTREGADA)) {
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
                if (equipo?.imagenUrl != null) {
                    AsyncImage(
                        model = equipo.imagenUrl,
                        contentDescription = "Imagen del equipo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                }

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
