package com.example.proyecto_bmi.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_bmi.ui.theme.Proyecto_bmiTheme
import com.example.proyecto_bmi.viewmodel.UsuarioViewModel
import com.example.proyecto_bmi.data.local.dao.UsuarioDao
import com.example.proyecto_bmi.data.local.entity.UsuarioEntity
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: UsuarioViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoginEnabled = email.isNotBlank() && password.length >= 6

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a la pestaña anterior",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(all = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Buscador de Manuales de Pesaje", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate("catalogo")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isLoginEnabled
            ) {
                Text("Iniciar Sesión")
            }

            Spacer(Modifier.height(16.dp))

            // Texto modificado según solicitud
            Text(
                text = buildAnnotatedString {
                    append("¿No tienes una cuenta? ")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("Registrate")
                    }
                },
                modifier = Modifier.clickable { navController.navigate("registro") }
            )
        }
    }
}

class PreviewAuthViewModel(repository: UsuarioRepository) : UsuarioViewModel(repository) {
}

private val loginScreenPreviewViewModel: PreviewAuthViewModel by lazy {
    val mockDao = MockUsuarioDao()
    val mockRepository = MockUsuarioRepository(mockDao)
    PreviewAuthViewModel(mockRepository)
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    Proyecto_bmiTheme {
        LoginScreen(navController = rememberNavController(), viewModel = loginScreenPreviewViewModel)
    }
}