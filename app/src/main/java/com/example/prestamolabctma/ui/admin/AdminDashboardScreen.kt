package com.example.prestamolabctma.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onVerPerfil: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Administrativo") },
                actions = {
                    IconButton(onClick = onVerPerfil) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Mi Perfil")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir")
                    }
                },
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AdminCard(
                    title = "Registrar Equipo",
                    icon = Icons.Default.Add
                ) { onNavigate("registro_equipo") }
            }
            item {
                AdminCard(
                    title = "Gestionar Solicitudes",
                    icon = Icons.Default.CheckCircle,
                ) { onNavigate("gestion_solicitudes") }
            }
            item {
                AdminCard(
                    title = "Reportes",
                    icon = Icons.Default.BarChart,
                ) { onNavigate("reportes") }
            }
            item {
                AdminCard(
                    title = "Equipos / Trazabilidad",
                    icon = Icons.AutoMirrored.Filled.List
                ) { onNavigate("catalogo_admin") }
            }
        }
    }
}

@Composable
fun AdminCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}
