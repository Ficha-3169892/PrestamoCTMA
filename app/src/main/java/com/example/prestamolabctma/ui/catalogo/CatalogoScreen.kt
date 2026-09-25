package com.example.prestamolabctma.ui.catalogo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolabctma.model.CategoriaEquipo
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.EstadoEquipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    equipos: List<Equipo>,
    searchQuery: String,
    categoriaSeleccionada: CategoriaEquipo?,
    onEquipoClick: (Int) -> Unit,
    onVerMisSolicitudes: () -> Unit,
    onBusquedaChange: (String) -> Unit,
    onCategoriaSelect: (CategoriaEquipo?) -> Unit,
    onToggleFavorito: (Int) -> Unit,
    onVerPerfil: () -> Unit,
    onBack: (() -> Unit)? = null,
) {
    val favoritos = equipos.filter { it.isFavorite }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PréstamoLab CTMA") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onVerPerfil) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                    }
                    Button(onClick = onVerMisSolicitudes) {
                        Text("Mis Préstamos")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // Barra de Búsqueda (HU-07)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onBusquedaChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar equipos...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
            )

            // Filtros de Categoría (HU-07)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = categoriaSeleccionada == null,
                        onClick = { onCategoriaSelect(null) },
                        label = { Text("Todos") }
                    )
                }
                items(CategoriaEquipo.entries) { categoria ->
                    FilterChip(
                        selected = categoriaSeleccionada == categoria,
                        onClick = { onCategoriaSelect(categoria) },
                        label = { Text(categoria.name) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Sección de Favoritos (HU-08)
                if ((favoritos.isNotEmpty()) && (searchQuery.isEmpty()) && (categoriaSeleccionada == null)) {
                    item {
                        Text(
                            text = "Equipos Frecuentes",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(favoritos) { equipo ->
                        EquipoItem(
                            equipo = equipo,
                            onClick = { onEquipoClick(equipo.id) }
                        ) { onToggleFavorito(equipo.id) }
                    }
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                item {
                    Text(
                        text = "Catálogo General",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(equipos) { equipo ->
                    EquipoItem(
                        equipo = equipo,
                        onClick = { onEquipoClick(equipo.id) }
                    ) { onToggleFavorito(equipo.id) }
                }
            }
        }
    }
}

@Composable
fun EquipoItem(
    equipo: Equipo,
    onClick: () -> Unit,
    onToggleFavorito: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = equipo.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = "Categoría: ${equipo.categoria}", style = MaterialTheme.typography.bodySmall)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleFavorito) {
                    Icon(
                        imageVector = if (equipo.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (equipo.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }
                StatusBadge(estado = equipo.estado)
            }
        }
    }
}

@Composable
fun StatusBadge(estado: EstadoEquipo) {
    val color = when (estado) {
        EstadoEquipo.DISPONIBLE -> MaterialTheme.colorScheme.primary
        EstadoEquipo.RESERVADO -> MaterialTheme.colorScheme.secondary
        EstadoEquipo.PRESTADO -> MaterialTheme.colorScheme.error
        EstadoEquipo.MANTENIMIENTO -> MaterialTheme.colorScheme.tertiary
        EstadoEquipo.FUERA_DE_SERVICIO -> MaterialTheme.colorScheme.outline
    }
    
    Surface(
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = estado.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
