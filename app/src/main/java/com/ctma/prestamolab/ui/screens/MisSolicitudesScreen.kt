package com.ctma.prestamolab.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo
import com.ctma.prestamolab.ui.state.ListadoUiState

/**
 * [HU 03] Gestión y Cancelación de Solicitudes.
 * [HU 04] Ficha Técnica Multimedia: Visualización de fotos.
 */
@Composable
fun MisSolicitudesScreen(
    solicitudesState: ListadoUiState<SolicitudPrestamo>,
    equipos: List<Equipo>,
    onBack: () -> Unit,
    onSolicitudClick: (Int) -> Unit,
) {
    var imagenUrlAmpliada by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Mis Préstamos", onBack = onBack)

        when (solicitudesState) {
            is ListadoUiState.Cargando -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ListadoUiState.Vacio -> {
                EmptyState("No has solicitado equipos todavía.")
            }
            is ListadoUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error al cargar solicitudes: ${solicitudesState.mensaje}")
                }
            }
            is ListadoUiState.Contenido -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(solicitudesState.datos, key = { it.id }) { solicitud ->
                        val equipo = equipos.find { it.id == solicitud.equipoId }
                        SolicitudCard(
                            solicitud = solicitud,
                            equipoNombre = equipo?.nombre ?: "Equipo desconocido",
                            equipoImagenUrl = equipo?.imagenUrl,
                            onClick = { onSolicitudClick(solicitud.id) },
                            onImageClick = { url -> imagenUrlAmpliada = url }
                        )
                    }
                }
            }
        }
    }

    if (imagenUrlAmpliada != null) {
        ImagenAmpliadaDialog(
            imageUrl = imagenUrlAmpliada!!,
            onDismiss = { imagenUrlAmpliada = null }
        )
    }
}

@Composable
private fun SolicitudCard(
    solicitud: SolicitudPrestamo,
    equipoNombre: String,
    equipoImagenUrl: String?,
    onClick: () -> Unit,
    onImageClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // [HU-04] Foto del equipo en la solicitud
            if (equipoImagenUrl != null) {
                AsyncImage(
                    model = equipoImagenUrl,
                    contentDescription = "Imagen de $equipoNombre",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onImageClick(equipoImagenUrl) },
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text("Préstamo #${solicitud.id}", style = MaterialTheme.typography.titleMedium)
                Text("Equipo: $equipoNombre")
                Text("Estado: ${solicitud.estado}", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun ImagenAmpliadaDialog(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Imagen ampliada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Fit
                )
                TextButton(onClick = onDismiss, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Cerrar")
                }
            }
        }
    }
}
