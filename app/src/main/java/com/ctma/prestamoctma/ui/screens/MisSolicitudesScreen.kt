package com.ctma.prestamoctma.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.ctma.prestamoctma.R
import com.ctma.prestamoctma.data.local.entities.EvidenciaEntity
import com.ctma.prestamoctma.data.local.entities.UploadStatus
import com.ctma.prestamoctma.model.EstadoSolicitud
import com.ctma.prestamoctma.model.GravedadDano
import com.ctma.prestamoctma.model.SolicitudPrestamo
import com.ctma.prestamoctma.ui.viewmodel.ListadoUiState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisSolicitudesScreen(
    listadoSolicitudes: ListadoUiState<SolicitudPrestamo>,
    evidencias: Map<String, List<EvidenciaEntity>> = emptyMap(),
    onCancelar: (String) -> Unit,
    onReportarDevolucion: (String, String, GravedadDano) -> Unit,
    onGuardarEvidencia: (String, String, Long, String) -> Unit,
    onBack: () -> Unit
) {
    var solicitudParaReportar by remember { mutableStateOf<SolicitudPrestamo?>(null) }
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    // Cámara
    var capturedUri by remember { mutableStateOf<Uri?>(null) }
    var currentPhotoFile by remember { mutableStateOf<File?>(null) }
    var currentSolicitudIdForPhoto by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && currentPhotoFile != null && currentSolicitudIdForPhoto != null) {
                if (currentPhotoFile!!.exists()) {
                    val fileUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        currentPhotoFile!!
                    )
                    onGuardarEvidencia(
                        currentSolicitudIdForPhoto!!,
                        fileUri.toString(),
                        currentPhotoFile!!.length(),
                        "image/jpeg"
                    )
                }
            }
        }
    )

    // Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null && currentSolicitudIdForPhoto != null) {
                try {
                    val resolver = context.contentResolver
                    val type = resolver.getType(uri) ?: "image/jpeg"
                    val directory = File(context.filesDir, "Pictures").apply { mkdirs() }
                    val destFile = File.createTempFile(
                        "GALERIA_${currentSolicitudIdForPhoto!!}_",
                        ".jpg",
                        directory
                    )
                    resolver.openInputStream(uri)?.use { inputStream ->
                        destFile.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    if (destFile.exists()) {
                        val fileUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            destFile
                        )
                        onGuardarEvidencia(
                            currentSolicitudIdForPhoto!!,
                            fileUri.toString(),
                            destFile.length(),
                            type
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.mis_solicitudes_desc)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_desc)
                        )
                    }
                },
                actions = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        TextButton(onClick = {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }) {
                            Text("Activar Recordatorios", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            val solicitudes = (listadoSolicitudes as? ListadoUiState.Contenido)?.items ?: emptyList()
            val vencidas = solicitudes.filter { it.estaVencida() }
            if (vencidas.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Tienes ${vencidas.size} solicitud(es) vencida(s). Por favor realiza la devolución.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            when (listadoSolicitudes) {
                is ListadoUiState.Cargando -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ListadoUiState.Vacio -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_solicitudes_msg))
                    }
                }
                is ListadoUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = listadoSolicitudes.mensaje, color = MaterialTheme.colorScheme.error)
                    }
                }
                is ListadoUiState.Contenido -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(listadoSolicitudes.items) { solicitud ->
                            SolicitudCard(
                                solicitud = solicitud, 
                                evidencias = evidencias[solicitud.id] ?: emptyList(),
                                onCancelar = { onCancelar(solicitud.id) },
                                onDevolver = { solicitudParaReportar = solicitud },
                                onTakePhoto = {
                                    val directory = File(context.filesDir, "Pictures").apply { mkdirs() }
                                    val photoFile = File.createTempFile(
                                        "EVIDENCIA_${solicitud.id}_",
                                        ".jpg",
                                        directory
                                    )
                                    currentPhotoFile = photoFile
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        photoFile
                                    )
                                    capturedUri = uri
                                    currentSolicitudIdForPhoto = solicitud.id
                                    cameraLauncher.launch(uri)
                                },
                                onPickPhoto = {
                                    currentSolicitudIdForPhoto = solicitud.id
                                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }
                            )
                        }
                    }
                }
            }
        }

        solicitudParaReportar?.let { solicitud ->
            DevolucionDialog(
                onDismiss = { solicitudParaReportar = null },
                onConfirm = { novedad, gravedad ->
                    onReportarDevolucion(solicitud.id, novedad, gravedad)
                    solicitudParaReportar = null
                }
            )
        }
    }
}

@Composable
fun SolicitudCard(
    solicitud: SolicitudPrestamo, 
    evidencias: List<EvidenciaEntity> = emptyList(),
    onCancelar: () -> Unit,
    onDevolver: () -> Unit,
    onTakePhoto: () -> Unit,
    onPickPhoto: () -> Unit
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val esVencida = solicitud.estaVencida()
    val faltaPoco = solicitud.faltaPocoParaVencer()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = if (esVencida) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
        } else if (faltaPoco) {
            CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (faltaPoco) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFBC02D), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "¡Atención! Vence en menos de 15 min",
                        color = Color(0xFFFBC02D),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = solicitud.nombreEquipo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StatusChip(estado = solicitud.estado)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Destino: ${solicitud.ambienteDestino}", style = MaterialTheme.typography.bodyMedium)
            Text(text = stringResource(R.string.desde_label, dateFormat.format(solicitud.fechaSolicitud)))
            Text(text = "Vence: ${dateFormat.format(solicitud.fechaVencimiento)}", style = MaterialTheme.typography.bodySmall)
            
            if (solicitud.novedadDetalle != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Novedad: ${solicitud.novedadDetalle}", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                Text(text = "Gravedad: ${solicitud.gravedadDano}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            }

            if (evidencias.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Evidencias (${evidencias.size}) - Toca para abrir:", style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    evidencias.forEach { ev ->
                        val urlToOpen = ev.remoteUrl ?: ev.localUri
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = 2.dp,
                                    color = when (ev.status) {
                                        UploadStatus.LOCAL -> Color.Gray
                                        UploadStatus.SUBIENDO -> MaterialTheme.colorScheme.primary
                                        UploadStatus.SINCRONIZADA -> Color(0xFF2E7D32)
                                        UploadStatus.FALLIDA -> MaterialTheme.colorScheme.error
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen)).apply {
                                            if (urlToOpen.startsWith("content://")) {
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                setDataAndType(Uri.parse(urlToOpen), "image/*")
                                            } else {
                                                setDataAndType(Uri.parse(urlToOpen), "image/*")
                                            }
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "No se puede abrir la imagen", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        ) {
                            AsyncImage(
                                model = urlToOpen,
                                contentDescription = "Evidencia",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Pequeña barra o indicador de estado inferior
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        when (ev.status) {
                                            UploadStatus.LOCAL -> Color.Gray.copy(alpha = 0.8f)
                                            UploadStatus.SUBIENDO -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                            UploadStatus.SINCRONIZADA -> Color(0xFF2E7D32).copy(alpha = 0.8f)
                                            UploadStatus.FALLIDA -> MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                        }
                                    )
                                    .padding(vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (ev.status == UploadStatus.SUBIENDO) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(10.dp),
                                        strokeWidth = 1.dp
                                    )
                                } else {
                                    Text(
                                        text = when (ev.status) {
                                            UploadStatus.LOCAL -> "LOCAL"
                                            UploadStatus.SUBIENDO -> "SUBIENDO"
                                            UploadStatus.SINCRONIZADA -> "OK"
                                            UploadStatus.FALLIDA -> "FALLA"
                                        },
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (solicitud.estado == EstadoSolicitud.ENTREGADA || solicitud.estado == EstadoSolicitud.SOLICITADA) {
                    IconButton(onClick = onTakePhoto) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Cámara")
                    }
                    IconButton(onClick = onPickPhoto) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "Galería")
                    }
                }

                if (solicitud.estado == EstadoSolicitud.SOLICITADA) {
                    OutlinedButton(
                        onClick = onCancelar,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(stringResource(R.string.cancelar_button))
                    }
                }
                
                if (solicitud.estado == EstadoSolicitud.SOLICITADA || solicitud.estado == EstadoSolicitud.ENTREGADA) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onDevolver) {
                        Text("Reportar Devolución")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevolucionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, GravedadDano) -> Unit
) {
    var novedad by remember { mutableStateOf("") }
    var gravedad by remember { mutableStateOf(GravedadDano.NINGUNA) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reportar Novedad de Devolución") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = novedad,
                    onValueChange = { novedad = it },
                    label = { Text("Detalle de la novedad/daño") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Gravedad: ${gravedad.name}")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        GravedadDano.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name) },
                                onClick = {
                                    gravedad = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(novedad, gravedad) }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun StatusChip(estado: EstadoSolicitud) {
    val color = when (estado) {
        EstadoSolicitud.SOLICITADA -> Color(0xFF2196F3)
        EstadoSolicitud.APROBADA -> Color(0xFF4CAF50)
        EstadoSolicitud.ENTREGADA -> Color(0xFF9C27B0)
        EstadoSolicitud.DEVUELTA -> Color(0xFF795548)
        EstadoSolicitud.CANCELADA -> Color.Gray
        EstadoSolicitud.RECHAZADA -> Color(0xFFF44336)
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = estado.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
