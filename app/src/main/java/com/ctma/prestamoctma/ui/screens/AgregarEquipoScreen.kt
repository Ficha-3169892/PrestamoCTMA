package com.ctma.prestamoctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ctma.prestamoctma.R
import com.ctma.prestamoctma.model.CategoriaEquipo
import com.ctma.prestamoctma.ui.viewmodel.OperacionUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarEquipoScreen(
    onAgregar: (String, CategoriaEquipo, String) -> Unit,
    onBack: () -> Unit,
    error: String?,
    operacionEstado: OperacionUiState = OperacionUiState.Idle,
    onDismissError: () -> Unit
) {
    val isLoading = operacionEstado == OperacionUiState.Ejecutando
    
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(CategoriaEquipo.COMPUTACION) }
    var descripcion by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            onDismissError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.agregar_equipo_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_desc)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text(stringResource(R.string.nombre_equipo_label)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = categoria.name,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(stringResource(R.string.categoria_label)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                if (!isLoading) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
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
                    // HACK: Clickable overlay for dropdown
                    Surface(
                        onClick = { expanded = !expanded },
                        color = Color.Transparent,
                        modifier = Modifier.matchParentSize()
                    ) {}
                }
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text(stringResource(R.string.descripcion_label)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                enabled = !isLoading
            )

            Button(
                onClick = { onAgregar(nombre, categoria, descripcion) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(stringResource(R.string.guardar_equipo_button))
                }
            }
        }
    }
}
