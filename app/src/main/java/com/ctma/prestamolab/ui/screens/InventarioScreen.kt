package com.ctma.prestamolab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(
    onAgregarEquipo: (Equipo) -> Unit,
    onBack: () -> Unit,
) {
    var nombre by remember { mutableStateOf("") }
    var serie by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(CategoriaEquipo.ELECTRONICA) }
    var especificaciones by remember { mutableStateOf("") }
    var accesorios by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(value = false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Inventario") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Agregar Nuevo Equipo", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del equipo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = serie,
                onValueChange = { serie = it },
                label = { Text("Número de Serie") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = marca,
                onValueChange = { marca = it },
                label = { Text("Marca") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = categoria.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    CategoriaEquipo.entries.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                categoria = cat
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = especificaciones,
                onValueChange = { especificaciones = it },
                label = { Text("Especificaciones Técnicas") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = accesorios,
                onValueChange = { accesorios = it },
                label = { Text("Accesorios (separados por coma)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación Física") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (nombre.isNotBlank() && serie.isNotBlank()) {
                        val equipo = Equipo(
                            id = 0, // Se asigna en repo
                            nombre = nombre,
                            serie = serie,
                            marca = marca,
                            categoria = categoria,
                            estado = EstadoEquipo.DISPONIBLE,
                            especificaciones = especificaciones,
                            accesorios = accesorios.split(",")
                            .asSequence()
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                            .toList(),
                            ubicacion = ubicacion
                        )
                        onAgregarEquipo(equipo)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nombre.isNotBlank() && serie.isNotBlank()
            ) {
                Text("Guardar Equipo")
            }
        }
    }
}
