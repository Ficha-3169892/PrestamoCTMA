package com.ctma.prestamolab.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.ui.state.ListadoUiState

/**
 * [HU 01] Consulta de Catálogo de Equipos.
 * [HU 07] Búsqueda y Filtro Avanzado.
 * [HU 08] Marcado de Equipos Frecuentes.
 */
@Composable
fun CatalogoScreen(
    equiposState: ListadoUiState<Equipo>,
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
    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(
            title = "PréstamoLab CTMA",
            subtitle = if (esAdministrador) "Panel de Administración" else "Catálogo de equipos",
            actionText = "Perfil",
            onAction = onPerfilClick,
        )

        // Botones Administrativos (Si aplica)
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

        // Buscador y Categorías
        OutlinedTextField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Buscar por nombre o serie...") },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
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
                        Text(categoria.name.lowercase().replaceFirstChar { it.uppercase() }) 
                    }
                )
            }
        }

        // Gestión de Estado de Listado (Semana 07)
        when (equiposState) {
            is ListadoUiState.Cargando -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ListadoUiState.Vacio -> {
                EmptyState("No hay equipos registrados en el CTMA.")
            }
            is ListadoUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${equiposState.mensaje}", color = MaterialTheme.colorScheme.error)
                }
            }
            is ListadoUiState.Contenido -> {
                val listaFiltrada = equiposState.datos.filter { equipo ->
                    val matchBusqueda = equipo.nombre.contains(busqueda, ignoreCase = true) ||
                            equipo.serie.contains(busqueda, ignoreCase = true)
                    val matchCat = categoriaSeleccionada == null || equipo.categoria == categoriaSeleccionada
                    matchBusqueda && matchCat
                }
                
                val favoritos = listaFiltrada.filter { it.esFavorito }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // Sección Favoritos (Solo si no hay búsqueda activa)
                    if (favoritos.isNotEmpty() && busqueda.isEmpty()) {
                        item {
                            Text("Favoritos", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                        }
                        items(favoritos, key = { "fav_${it.id}" }) { equipo ->
                            EquipoCard(equipo, { onEquipoClick(equipo.id) }, { onFavoritoClick(equipo.id) })
                        }
                        item { HorizontalDivider(Modifier.padding(16.dp)) }
                    }

                    item {
                        Text("Catálogo General", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                    }

                    items(listaFiltrada, key = { it.id }) { equipo ->
                        EquipoCard(equipo, { onEquipoClick(equipo.id) }, { onFavoritoClick(equipo.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun EquipoCard(equipo: Equipo, onClick: () -> Unit, onFavoritoClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(equipo.nombre, style = MaterialTheme.typography.titleMedium)
                Text("Serie: ${equipo.serie}", style = MaterialTheme.typography.bodySmall)
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
