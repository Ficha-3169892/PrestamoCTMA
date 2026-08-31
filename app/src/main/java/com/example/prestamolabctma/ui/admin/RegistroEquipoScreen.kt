package com.example.prestamolabctma.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolabctma.model.CategoriaEquipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroEquipoScreen(
    onRegistrar: (String, String, CategoriaEquipo, String, String, String, String, String) -> Unit,
    onBack: () -> Unit,
    error: String?,
) {
    var nombre by remember { mutableStateOf("") }
    var serie by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(CategoriaEquipo.OTROS) }
    var ubicacion by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var specs by remember { mutableStateOf("") }
    var accesorios by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(value = false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Nuevo Equipo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del Equipo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = serie, onValueChange = { serie = it }, label = { Text("Número de Serie") }, modifier = Modifier.fillMaxWidth())
            
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
                    modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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

            OutlinedTextField(value = ubicacion, onValueChange = { ubicacion = it }, label = { Text("Ubicación en Laboratorio") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = specs, onValueChange = { specs = it }, label = { Text("Especificaciones") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            OutlinedTextField(value = accesorios, onValueChange = { accesorios = it }, label = { Text("Accesorios (separados por coma)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("URL Imagen (Unsplash)") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = { onRegistrar(nombre, serie, categoria, ubicacion, marca, specs, accesorios, url) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar en Inventario")
            }
        }
    }
}
