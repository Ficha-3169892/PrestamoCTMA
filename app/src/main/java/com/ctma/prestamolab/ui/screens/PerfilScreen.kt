package com.ctma.prestamolab.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.Usuario

/**
 * [HU 06] Gestión de Perfil de Usuario.
 * [HU 04] Actualización Multimedia (S9).
 */
@Composable
fun PerfilScreen(
    usuario: Usuario?,
    onBack: () -> Unit,
    onActualizar: (String, String?) -> Unit,
    onCerrarSesion: () -> Unit,
) {
    if (usuario == null) return

    var telefono by remember { mutableStateOf(usuario.telefono) }
    var correoAlt by remember { mutableStateOf(usuario.correoAlternativo ?: "") }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // En una implementación real, aquí se subiría a Supabase Storage
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Mi Perfil", onBack = onBack)
        
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoCampo("Nombre", usuario.nombre)
            InfoCampo("Documento", usuario.documento)
            InfoCampo("Ficha", usuario.ficha)
            InfoCampo("Correo Institucional", usuario.correoInstitucional)
            
            HorizontalDivider()
            
            Text("Información de contacto (editable)", style = MaterialTheme.typography.titleSmall)

            OutlinedButton(
                onClick = {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cambiar Foto de Perfil")
            }
            
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = correoAlt,
                onValueChange = { correoAlt = it },
                label = { Text("Correo Alternativo") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Button(
                onClick = { onActualizar(telefono, correoAlt.ifEmpty { null }) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar cambios")
            }
            
            OutlinedButton(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
private fun InfoCampo(label: String, valor: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}
