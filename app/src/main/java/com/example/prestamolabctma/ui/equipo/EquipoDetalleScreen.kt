package com.example.prestamolabctma.ui.equipo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.EstadoEquipo
import com.example.prestamolabctma.ui.catalogo.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoDetalleScreen(
    equipo: Equipo?,
    onBack: () -> Unit,
    onSolicitar: (Int) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Equipo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (equipo == null) {
                Text(
                    text = "Error: El equipo solicitado no existe.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp),
                )
            } else {
                // Imagen del Equipo (HU-04 Multimedia)
                AsyncImage(
                    model = equipo.imageUrl,
                    contentDescription = "Imagen de ${equipo.nombre}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop,
                )

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(text = equipo.nombre, style = MaterialTheme.typography.headlineMedium)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Marca: ${equipo.marca}", style = MaterialTheme.typography.titleMedium)
                        StatusBadge(estado = equipo.estado)
                    }

                    HorizontalDivider()

                    // Sección: Ficha Técnica (HU-04)
                    SectionHeader(icon = Icons.Default.Info, title = "Ficha Técnica")
                    TechnicalRow(label = "Número de Serie", value = equipo.numSerie)
                    TechnicalRow(label = "Categoría", value = equipo.categoria.name)
                    TechnicalRow(label = "Especificaciones", value = equipo.especificaciones)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sección: Accesorios (HU-04)
                    SectionHeader(icon = Icons.AutoMirrored.Filled.List, title = "Accesorios Incluidos")
                    Text(text = equipo.accesorios, style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sección: Ubicación (HU-04)
                    SectionHeader(icon = Icons.Default.LocationOn, title = "Ubicación de Entrega")
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = equipo.ubicacion,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { onSolicitar(equipo.id) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = equipo.estado == EstadoEquipo.DISPONIBLE,
                    ) {
                        Text("Solicitar Préstamo")
                    }
                    
                    if (equipo.estado != EstadoEquipo.DISPONIBLE) {
                        Text(
                            text = "Este equipo no se encuentra disponible para préstamo en este momento.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun TechnicalRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = "$label:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
        )
    }
}
