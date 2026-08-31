package com.ctma.prestamolab.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo

@Composable
fun CatalogoScreen(
    equipos: List<Equipo>,
    busqueda: String,
    categoriaSeleccionada: CategoriaEquipo?,
    esAdministrador: Boolean,
    onBusquedaChange: (String) -> Unit,
    onCategoriaChange: (CategoriaEquipo?) -> Unit,
    onEquipoClick: (Int) -> Unit,
    onMisSolicitudesClick: () -> Unit,
    onGestionAdminClick: () -> Unit,
    onEstadisticasClick: () -> Unit,
    onInventarioClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onFavoritoClick: (Int) -> Unit,
) {
    val favoritos = equipos.filter { it.esFavorito }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(
            title = "PréstamoLab CTMA",
            subtitle = if (esAdministrador) "Panel de Administración" else "Catálogo de equipos",
            actionText = "Perfil",
            onAction = onPerfilClick,
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            if (esAdministrador) {
                TextButton(onClick = onInventarioClick) { Text("Inventario") }
                TextButton(onClick = onGestionAdminClick) { Text("Gestión") }
                TextButton(onClick = onEstadisticasClick) { Text("Estadísticas") }
            }
            TextButton(onClick = onMisSolicitudesClick) {
                Text("Mis solicitudes")
            }
        }

        OutlinedTextField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Buscar por nombre, serie o marca...") },
            singleLine = true
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
        ) {
            item {
                FilterChip(
                    selected = categoriaSeleccionada == null,
                    onClick = { onCategoriaChange(null) },
                    label = { Text("Todas") }
                )
            }
            items(CategoriaEquipo.entries) { categoria ->
                FilterChip(
                    selected = categoriaSeleccionada == categoria,
                    onClick = { onCategoriaChange(categoria) },
                    label = { 
                        val nombre = categoria.name.lowercase().replaceFirstChar { it.uppercase() }
                        Text(nombre) 
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (favoritos.isNotEmpty() && (busqueda.isEmpty()) && (categoriaSeleccionada == null)) {
                item {
                    Text(
                        "Mis Favoritos",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(favoritos, key = { "fav_${it.id}" }) { equipo ->
                    EquipoCard(
                        equipo = equipo, 
                        onClick = { onEquipoClick(equipo.id) },
                        onFavoritoClick = { onFavoritoClick(equipo.id) },
                    )
                }
                item { HorizontalDivider(modifier = Modifier.padding(16.dp)) }
            }

            item {
                Text(
                    "Todos los equipos",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(equipos, key = { it.id }) { equipo ->
                EquipoCard(
                    equipo = equipo, 
                    onClick = { onEquipoClick(equipo.id) },
                    onFavoritoClick = { onFavoritoClick(equipo.id) },
                )
            }
            
            if (equipos.isEmpty()) {
                item {
                    Text(
                        "No se encontraron equipos con los criterios de búsqueda.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun EquipoCard(equipo: Equipo, onClick: () -> Unit, onFavoritoClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .semantics {
                contentDescription = "${equipo.nombre}, ${equipo.categoria}, estado ${equipo.estado}"
            }
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(equipo.nombre, style = MaterialTheme.typography.titleMedium)
                Text("Serie: ${equipo.serie}", style = MaterialTheme.typography.bodySmall)
                Text("Categoría: ${equipo.categoria}", style = MaterialTheme.typography.bodyMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                EstadoEquipoChip(equipo.estado)
                IconButton(onClick = onFavoritoClick) {
                    Text(if (equipo.esFavorito) "❤️" else "🤍")
                }
            }
        }
    }
}
