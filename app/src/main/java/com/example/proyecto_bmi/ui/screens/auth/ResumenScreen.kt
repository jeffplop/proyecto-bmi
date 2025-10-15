package com.example.proyecto_bmi.ui.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_bmi.ui.theme.Proyecto_bmiTheme
import com.example.proyecto_bmi.viewmodel.UsuarioViewModel
import com.example.proyecto_bmi.data.local.dao.UsuarioDao
import com.example.proyecto_bmi.data.local.entity.UsuarioEntity
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import com.example.proyecto_bmi.domain.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen de Registro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar a la pantalla anterior",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(all = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "¡Registro Exitoso!", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            Text(text = "Resumen del Registro", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            Text(text = "Nombre: ${estado.nombre}")
            Text(text = "Correo: ${estado.correo}")
            Text(text = "Dirección: ${estado.direccion}")
            Text(text = "Contraseña: ${"*".repeat(n = estado.clave.length)}")
            Text(text = "Términos: ${if (estado.aceptaTerminos) "Aceptados" else "No aceptados"}")

            Spacer(Modifier.height(32.dp))

            Button(onClick = { navController.navigate("catalogo") }) {
                Text("Ir a Catálogo")
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = { navController.navigate("contact") }) {
                Text("Contactar")
            }
        }
    }
}

class MockUsuarioDao : UsuarioDao {
    override suspend fun insertUser(user: UsuarioEntity): Long = 1L
    override suspend fun getUserByCredentials(email: String, clave: String): UsuarioEntity? = null
    override suspend fun getUserById(userId: Int): UsuarioEntity? = null
    override suspend fun updateTipoUsuario(userId: Int, nuevoTipo: String) {}
}

class MockUsuarioRepository(usuarioDao: UsuarioDao) : UsuarioRepository(usuarioDao)

class PreviewAuthViewModel(repository: UsuarioRepository) : UsuarioViewModel(repository) {
    override val estado: StateFlow<UsuarioUiState> = MutableStateFlow(
        UsuarioUiState(
            nombre = "Test Preview",
            correo = "test@example.com",
            clave = "12345678",
            confirmClave = "12345678",
            direccion = "Calle Preview 123",
            aceptaTerminos = true
        )
    )
}

private val resumenScreenPreviewViewModel: PreviewAuthViewModel by lazy {
    val mockDao = MockUsuarioDao()
    val mockRepository = MockUsuarioRepository(mockDao)
    PreviewAuthViewModel(mockRepository)
}

@Preview(showBackground = true)
@Composable
fun ResumenScreenPreview() {
    Proyecto_bmiTheme {
        ResumenScreen(navController = rememberNavController(), viewModel = resumenScreenPreviewViewModel)
    }
}