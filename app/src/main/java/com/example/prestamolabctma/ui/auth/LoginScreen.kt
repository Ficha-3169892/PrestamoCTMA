package com.example.prestamolabctma.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.prestamolabctma.model.RolUsuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLogin: (String, RolUsuario) -> Unit,
    error: String?,
    isLoading: Boolean,
) {
    var email by remember { mutableStateOf("") }
    var rolSeleccionado by remember { mutableStateOf(RolUsuario.APRENDIZ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "PréstamoLab CTMA",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Gestión de Préstamos y Laboratorios SENA",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Seleccione su Rol e Inicie Sesión",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de Rol (Aprendiz vs Administrador)
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            SegmentedButton(
                selected = (rolSeleccionado == RolUsuario.APRENDIZ),
                onClick = {
                    rolSeleccionado = RolUsuario.APRENDIZ
                    if (email.isBlank()) email = "estudiante@soy.sena.edu.co"
                },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                icon = { Icon(Icons.Default.School, contentDescription = null) }
            ) {
                Text("Aprendiz")
            }

            SegmentedButton(
                selected = (rolSeleccionado == RolUsuario.ADMINISTRADOR),
                onClick = {
                    rolSeleccionado = RolUsuario.ADMINISTRADOR
                    if (email.isBlank()) email = "instructor@sena.edu.co"
                },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) }
            ) {
                Text("Administrador")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Institucional SENA") },
            placeholder = { Text(if (rolSeleccionado == RolUsuario.ADMINISTRADOR) "instructor@sena.edu.co" else "aprendiz@soy.sena.edu.co") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = error != null,
            singleLine = true,
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onLogin(email, rolSeleccionado) },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && !isLoading,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(if (rolSeleccionado == RolUsuario.ADMINISTRADOR) "Ingresar como Administrador" else "Ingresar como Aprendiz")
            }
        }
    }
}
