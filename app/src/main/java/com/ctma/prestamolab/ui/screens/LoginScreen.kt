package com.ctma.prestamolab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.ui.viewmodel.AuthUiState

/**
 * [HU 05] Autenticación y Gestión de Roles.
 */
@Composable
fun LoginScreen(
    state: AuthUiState,
    onLogin: (String, String) -> Unit,
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("PréstamoLab CTMA", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Inicia sesión con tu correo SENA", style = MaterialTheme.typography.bodyMedium)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo Institucional") },
            placeholder = { Text("ejemplo@soy.sena.edu.co") },
            supportingText = { state.erroresLogin.correo?.let { Text(it) } },
            isError = state.erroresLogin.correo != null,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            supportingText = { state.erroresLogin.contrasena?.let { Text(it) } },
            isError = state.erroresLogin.contrasena != null,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { onLogin(correo, contrasena) },
            enabled = !state.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Ingresar")
            }
        }
    }
}
