package com.example.prestamolabctma.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.prestamolabctma.model.Usuario
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    usuario: Usuario?,
    onBack: () -> Unit,
    onActualizar: (String, String) -> Unit,
    onLogout: () -> Unit,
) {
    if (usuario == null) return

    val context = LocalContext.current
    var telefono by remember { mutableStateOf(usuario.telefono) }
    var correoAlt by remember { mutableStateOf(usuario.correoAlternativo) }
    var editando by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para Photo Picker (Selección de imágenes sin permisos masivos - Semana 9)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            photoUri = uri
        }
    }

    // Launcher para Captura con Cámara (FileProvider - Semana 9)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            photoUri = tempCameraUri
        }
    }

    fun crearTempImageUri(): Uri {
        val dir = File(context.filesDir, "evidencias").apply { mkdirs() }
        val file = File(dir, "foto_perfil_${UUID.randomUUID()}.jpg").apply { createNewFile() }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Text("Salir", color = MaterialTheme.colorScheme.error)
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sección de Foto de Perfil (Coil + Photo Picker + Cámara - Semana 9)
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = photoUri ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
            }

            // Botones de Selección/Captura Multimedia
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
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
                    }
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cámara")
                }
            }

            HorizontalDivider()

            // Campos No Editables (HU-06)
            Column(modifier = Modifier.fillMaxWidth()) {
                InfoField(label = "Nombre", value = usuario.nombre)
                InfoField(label = "Documento", value = usuario.documento)
                InfoField(label = "Ficha de Formación", value = usuario.ficha)
                InfoField(label = "Correo Institucional", value = usuario.correoInstitucional)
            }

            HorizontalDivider()

            // Campos Editables (HU-06)
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono de Contacto") },
                modifier = Modifier.fillMaxWidth(),
                enabled = editando
            )

            OutlinedTextField(
                value = correoAlt,
                onValueChange = { correoAlt = it },
                label = { Text("Correo Alternativo") },
                modifier = Modifier.fillMaxWidth(),
                enabled = editando
            )

            Button(
                onClick = {
                    if (editando) {
                        onActualizar(telefono, correoAlt)
                    }
                    editando = !editando
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (editando) "Guardar Cambios" else "Editar Contacto")
            }
        }
    }
}

@Composable
fun InfoField(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
    }
}
