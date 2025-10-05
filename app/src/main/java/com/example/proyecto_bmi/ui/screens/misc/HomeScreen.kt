package com.example.proyecto_bmi.ui.screens.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_bmi.ui.theme.Proyecto_bmiTheme

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("B.M.I. Catálogo Principal", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(32.dp))
        Button(onClick = { navController.navigate("registro") }) {
            Text("Registrarse")
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { navController.navigate("contact") }) {
            Text("Contacto")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Proyecto_bmiTheme {
        HomeScreen(navController = rememberNavController())
    }
}