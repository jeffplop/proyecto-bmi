package com.example.proyecto_bmi.ui.screens.misc

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_bmi.ui.theme.Proyecto_bmiTheme

@Composable
fun ContactScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Contacto", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Tu Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Tu Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = mensaje,
            onValueChange = { mensaje = it },
            label = { Text("Describe tu problema") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp)
        )
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                navController.navigate("home")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = nombre.isNotBlank() && correo.isNotBlank() && mensaje.isNotBlank()
        ) {
            Text("Enviar Mensaje")
        }
        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController.navigate("home") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a Inicio")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactScreenPreview() {
    Proyecto_bmiTheme {
        ContactScreen(navController = rememberNavController())
    }
}