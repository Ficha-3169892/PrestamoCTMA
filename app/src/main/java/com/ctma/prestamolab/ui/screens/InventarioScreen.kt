package com.ctma.prestamolab.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import java.io.File

/**
 * [HU 09] Registro de Nuevos Equipos en Inventario.
 * [HU 04] Captura Multimedia (S9).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(
    onAgregarEquipo: (Equipo) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var serie by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(CategoriaEquipo.ELECTRONICA) }
    var especificaciones by remember { mutableStateOf("") }
    var accesorios by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf<String?>(null) }

    var expanded by remember { mutableStateOf(value = false) }

    // [Semana 09] Lanzadores para Photo Picker y Cámara Segura
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imagenUrl = uri.toString()
        }
    }

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    val takePhoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imagenUrl = tempImageUri.toString()
        }
    }

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

            // [Semana 09] UI para captura multimedia
            Text("Multimedia del equipo", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Galería")
                }
                OutlinedButton(
                    onClick = {
                        val file = File(context.cacheDir, "images/temp_photo.jpg")
                        file.parentFile?.mkdirs()
                        tempImageUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                        takePhoto.launch(tempImageUri!!)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cámara")
                }
            }
            
            if (imagenUrl != null) {
                Text("Imagen adjunta correctamente", color = MaterialTheme.colorScheme.primary)
            }

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
                            ubicacion = ubicacion,
                            imagenUrl = imagenUrl
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
