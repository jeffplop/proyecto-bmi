package com.example.proyecto_bmi.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyecto_bmi.model.UsuarioUiState

@Composable
fun ResumenScreen(
    estadoUi: UsuarioUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        Text("Resumen del registro", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(32.dp))

        Text("✅ ¡Registro Exitoso! Estos son tus datos:", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        Text("Nombre: ${estadoUi.nombre}")
        Text("Correo: ${estadoUi.correo}")
        Text("Clave: ***********")
        Text("Dirección: ${estadoUi.direccion}")

        Spacer(Modifier.height(32.dp))

        Button(onClick = onBackClick) {
            Text("Volver a Inicio")
        }
    }
}