package com.ctma.prestamolab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo
import com.ctma.prestamolab.ui.state.ListadoUiState

/**
 * [HU 03] Gestión y Cancelación de Solicitudes.
 */
@Composable
fun MisSolicitudesScreen(
    solicitudesState: ListadoUiState<SolicitudPrestamo>,
    equipos: List<Equipo>,
    onBack: () -> Unit,
    onSolicitudClick: (Int) -> Unit,
) {
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
                            onClick = { onSolicitudClick(solicitud.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SolicitudCard(
    solicitud: SolicitudPrestamo,
    equipoNombre: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Préstamo #${solicitud.id}", style = MaterialTheme.typography.titleMedium)
            Text("Equipo: $equipoNombre")
            Text("Estado: ${solicitud.estado}", color = MaterialTheme.colorScheme.primary)
        }
    }
}
