package com.example.proyecto_bmi.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_bmi.model.UsuarioUiState

@Composable
fun RegistroScreen(
    estadoUi: UsuarioUiState,
    onNombreChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onClaveChange: (String) -> Unit,
    onDireccionChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registro de Usuario", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

        FormTextField(
            label = "Nombre",
            value = estadoUi.nombre,
            onValueChange = onNombreChange,
            error = estadoUi.errores.nombre
        )
        Spacer(Modifier.height(16.dp))

        FormTextField(
            label = "Correo",
            value = estadoUi.correo,
            onValueChange = onCorreoChange,
            error = estadoUi.errores.correo
        )
        Spacer(Modifier.height(16.dp))

        FormTextField(
            label = "Clave",
            value = estadoUi.clave,
            onValueChange = onClaveChange,
            error = estadoUi.errores.clave
        )
        Spacer(Modifier.height(16.dp))

        FormTextField(
            label = "Dirección",
            value = estadoUi.direccion,
            onValueChange = onDireccionChange,
            error = estadoUi.errores.direccion
        )
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Registrar", fontSize = 18.sp)
        }
    }
}

@Composable
private fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}