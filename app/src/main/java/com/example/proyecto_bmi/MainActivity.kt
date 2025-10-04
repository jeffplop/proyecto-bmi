package com.example.proyecto_bmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_bmi.ui.theme.Proyecto_bmiTheme

// Clase de datos simple para guardar la información del registro.
data class UserData(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = ""
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Proyecto_bmiTheme {
                AppNavigation() // Usamos un composable para manejar la navegación simple
            }
        }
    }
}

/**
 * Gestiona la navegación simple entre el formulario y el resumen.
 */
@Composable
fun AppNavigation() {
    // Estado para guardar la información registrada. null significa que no se ha registrado.
    var userData by remember { mutableStateOf<UserData?>(null) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (userData == null) {
                // Muestra el formulario de registro si aún no hay datos.
                RegistrationFormScreen { data ->
                    userData = data // Actualiza el estado para ir a la pantalla de resumen
                }
            } else {
                // Muestra el resumen del registro con los datos si ya se registraron.
                RegistrationSummaryScreen(userData!!) {
                    userData = null // Permite volver a la pantalla de registro
                }
            }
        }
    }
}

// --------------------------------------------------------------------------

/**
 * Pantalla de Formulario de Registro.
 */
@Composable
fun RegistrationFormScreen(onRegisterClick: (UserData) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registro de Usuario", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

        // Campo para Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // Campo para Correo
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // Campo para Clave
        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it },
            label = { Text("Clave") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // Campo para Dirección
        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(32.dp))

        // Botón de Registro
        Button(
            onClick = {
                // Aquí se podría añadir validación (omitiendo por simplicidad)
                val newUserData = UserData(nombre, correo, clave, direccion)
                onRegisterClick(newUserData)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = nombre.isNotBlank() && correo.isNotBlank() && clave.isNotBlank() && direccion.isNotBlank()
        ) {
            Text("Registrar", fontSize = 18.sp)
        }
    }
}

// --------------------------------------------------------------------------

/**
 * Pantalla de Resumen del Registro.
 */
@Composable
fun RegistrationSummaryScreen(userData: UserData, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        // Título principal: "Resumen del registro"
        Text("Resumen del registro", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(32.dp))

        // Resumen de los datos registrados
        Text("✅ ¡Registro Exitoso! Estos son tus datos:", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        Text("Nombre: ${userData.nombre}")
        Text("Correo: ${userData.correo}")
        Text("Clave: ***********") // Ocultamos la clave por seguridad
        Text("Dirección: ${userData.direccion}")

        Spacer(Modifier.height(32.dp))

        // Botón para volver al formulario (solo como ejemplo)
        Button(onClick = onBackClick) {
            Text("Volver al Registro")
        }
    }
}

// --------------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun RegistrationFormPreview() {
    Proyecto_bmiTheme {
        RegistrationFormScreen {}
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationSummaryPreview() {
    Proyecto_bmiTheme {
        RegistrationSummaryScreen(UserData("Jeff Plop", "jp@email.com", "123456", "Calle Falsa 123")) {}
    }
}