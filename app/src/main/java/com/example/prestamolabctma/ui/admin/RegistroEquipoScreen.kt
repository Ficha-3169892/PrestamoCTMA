package com.example.prestamolabctma.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.prestamolabctma.model.CategoriaEquipo
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroEquipoScreen(
    onRegistrar: (String, String, CategoriaEquipo, String, String, String, String, String) -> Unit,
    onBack: () -> Unit,
    error: String?,
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var serie by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(CategoriaEquipo.ELECTRONICA) }
    var ubicacion by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var specs by remember { mutableStateOf("") }
    var accesorios by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            photoUri = uri
            url = uri.toString()
        }
    }

    // Camera Capture
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            photoUri = tempCameraUri
            url = tempCameraUri.toString()
        }
    }

    fun crearTempImageUri(): Uri {
        val dir = File(context.filesDir, "evidencias").apply { mkdirs() }
        val file = File(dir, "equipo_${UUID.randomUUID()}.jpg").apply { createNewFile() }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

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

            // Vista previa de la foto del equipo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (url.isNotBlank() || photoUri != null) {
                        AsyncImage(
                            model = photoUri ?: url,
                            contentDescription = "Foto del equipo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        Text(
                            text = "Sin foto seleccionada",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Botones multimedia para foto (Galería o Cámara)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Galería")
                }

                OutlinedButton(
                    onClick = {
                        val uri = crearTempImageUri()
                        tempCameraUri = uri
                        cameraLauncher.launch(uri)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cámara")
                }
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Equipo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = serie,
                onValueChange = { serie = it },
                label = { Text("Número de Serie Único") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
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

            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación en Laboratorio") },
                placeholder = { Text("Ej. Estante 2 - Lab 201") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = marca,
                onValueChange = { marca = it },
                label = { Text("Marca / Fabricante") },
                placeholder = { Text("Ej. Fluke, Cisco, Dell") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = specs,
                onValueChange = { specs = it },
                label = { Text("Especificaciones Técnicas / Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            OutlinedTextField(
                value = accesorios,
                onValueChange = { accesorios = it },
                label = { Text("Accesorios Incluidos") },
                placeholder = { Text("Ej. Cable USB, Puntas de prueba, Estuche") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = url,
                onValueChange = {
                    url = it
                    photoUri = null
                },
                label = { Text("URL de Imagen Alternativa (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    val finalUrl = if (url.isBlank()) "https://images.unsplash.com/photo-1581244276894-067d73010b95?w=800" else url
                    onRegistrar(nombre, serie, categoria, ubicacion, marca, specs, accesorios, finalUrl)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nombre.isNotBlank() && serie.isNotBlank()
            ) {
                Text("Guardar Equipo en Inventario")
            }
        }
    }
}
