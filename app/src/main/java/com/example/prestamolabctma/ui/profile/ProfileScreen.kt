package com.example.prestamolabctma.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolabctma.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    usuario: Usuario?,
    onBack: () -> Unit,
    onActualizar: (String, String) -> Unit,
    onLogout: () -> Unit,
) {
    if (usuario == null) return

    var telefono by remember { mutableStateOf(usuario.telefono) }
    var correoAlt by remember { mutableStateOf(usuario.correoAlternativo) }
    var editando by remember { mutableStateOf(value = false) }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campos No Editables (HU-06)
            InfoField(label = "Nombre", value = usuario.nombre)
            InfoField(label = "Documento", value = usuario.documento)
            InfoField(label = "Ficha de Formación", value = usuario.ficha)
            InfoField(label = "Correo Institucional", value = usuario.correoInstitucional)

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
