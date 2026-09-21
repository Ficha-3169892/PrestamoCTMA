package com.ctma.prestamoctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ctma.prestamoctma.R
import com.ctma.prestamoctma.model.Equipo
import com.ctma.prestamoctma.model.EstadoEquipo
import com.ctma.prestamoctma.model.SolicitudPrestamo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    equipos: List<Equipo>,
    solicitudes: List<SolicitudPrestamo> = emptyList(),
    error: String?,
    onEquipoClick: (Equipo) -> Unit,
    onVerSolicitudes: () -> Unit,
    onAgregarEquipo: () -> Unit,
    onDismissError: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Criterio 1: Genera alerta en pantalla cuando falten 15 minutos (vía Snackbar)
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            onDismissError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onVerSolicitudes) {
                        BadgedBox(
                            badge = {
                                val vencidasCount = solicitudes.count { it.estaVencida() }
                                if (vencidasCount > 0) {
                                    Badge { Text(vencidasCount.toString()) }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = stringResource(R.string.mis_solicitudes_desc)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregarEquipo) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.agregar_equipo_fab_desc)
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Criterio 2: Aviso visual persistente si la solicitud se encuentra vencida
            val vencidasCount = solicitudes.count { it.estaVencida() }
            if (vencidasCount > 0) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning, 
                            contentDescription = null, 
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.vencida_banner_msg, vencidasCount),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (equipos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.catalogo_title),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(equipos) { equipo ->
                        EquipoCard(equipo = equipo, onClick = { onEquipoClick(equipo) })
                    }
                }
            }
        }
    }
}

@Composable
fun EquipoCard(equipo: Equipo, onClick: () -> Unit) {
    val estaDisponible = equipo.estado == EstadoEquipo.DISPONIBLE
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = estaDisponible,
        colors = CardDefaults.cardColors(
            containerColor = if (estaDisponible) 
                MaterialTheme.colorScheme.surfaceVariant 
            else 
                Color.LightGray.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = equipo.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Badge(
                    containerColor = when(equipo.estado) {
                        EstadoEquipo.DISPONIBLE -> Color(0xFF4CAF50)
                        EstadoEquipo.RESERVADO -> Color(0xFFFF9800)
                        EstadoEquipo.PRESTADO -> Color(0xFFF44336)
                        EstadoEquipo.EN_MANTENIMIENTO -> Color.Gray
                    }
                ) {
                    Text(equipo.estado.name, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = equipo.categoria.name, style = MaterialTheme.typography.labelMedium)
            if (equipo.descripcion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = equipo.descripcion, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
