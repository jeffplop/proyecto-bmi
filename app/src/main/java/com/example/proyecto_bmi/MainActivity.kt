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

data class UserData(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = ""
)

enum class AppScreen {
    RegistrationForm,
    Summary,
    Contact
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Proyecto_bmiTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var userData by remember { mutableStateOf<UserData?>(null) }
    var currentScreen by remember { mutableStateOf(AppScreen.RegistrationForm) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                AppScreen.RegistrationForm -> {
                    RegistrationFormScreen(
                        onRegisterClick = { data ->
                            userData = data
                            currentScreen = AppScreen.Summary
                        }
                    )
                }
                AppScreen.Summary -> {
                    userData?.let {
                        RegistrationSummaryScreen(
                            it,
                            onBackClick = { currentScreen = AppScreen.RegistrationForm },
                            onContactClick = { currentScreen = AppScreen.Contact }
                        )
                    }
                }
                AppScreen.Contact -> {
                    ContactScreen {
                        currentScreen = AppScreen.RegistrationForm
                    }
                }
            }
        }
    }
}

@Composable
fun RegistrationFormScreen(
    onRegisterClick: (UserData) -> Unit
) {
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

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = clave, onValueChange = { clave = it }, label = { Text("Clave") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
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

@Composable
fun RegistrationSummaryScreen(
    userData: UserData,
    onBackClick: () -> Unit,
    onContactClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        Text("Resumen del registro", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(32.dp))

        Text("¡Registro Exitoso! Estos son tus datos:", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        Text("Nombre: ${userData.nombre}")
        Text("Correo: ${userData.correo}")
        Text("Clave: ***********")
        Text("Dirección: ${userData.direccion}")

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al Registro")
        }
        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onContactClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Contacto")
        }
    }
}

@Composable
fun ContactScreen(onBackClick: () -> Unit) {
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Contacto y Reporte de Error", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("Asunto") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Describe tu error o problema") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp)
        )
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                onBackClick()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = subject.isNotBlank() && message.isNotBlank()
        ) {
            Text("Enviar Reporte", fontSize = 18.sp)
        }
        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationFormPreview() {
    Proyecto_bmiTheme {
        RegistrationFormScreen(onRegisterClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationSummaryPreview() {
    Proyecto_bmiTheme {
        RegistrationSummaryScreen(UserData("Jeff Plop", "jp@email.com", "123456", "Calle Falsa 123"), {}, {})
    }
}

@Preview(showBackground = true)
@Composable
fun ContactScreenPreview() {
    Proyecto_bmiTheme {
        ContactScreen {}
    }
}