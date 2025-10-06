package com.example.proyecto_bmi.ui.screens.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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

@Composable
fun ResumenScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.estado.collectAsState()

    Column(
        Modifier
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

        Button(onClick = { navController.navigate("home") }) {
            Text("Ir a Inicio")
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = { navController.navigate("contact") }) {
            Text("Contactar")
        }
    }
}

private class MockUsuarioDao : UsuarioDao {
    override suspend fun insertUser(user: UsuarioEntity): Long = 1L

    override suspend fun getUserByCredentials(email: String, clave: String): UsuarioEntity? = null

    override suspend fun getUserById(userId: Int): UsuarioEntity? = null
    override suspend fun updateTipoUsuario(userId: Int, nuevoTipo: String) {}
}

private class MockUsuarioRepository(usuarioDao: UsuarioDao) : UsuarioRepository(usuarioDao)

private class PreviewUsuarioViewModel(repository: UsuarioRepository) : UsuarioViewModel(repository) {

    override val estado: StateFlow<UsuarioUiState> = MutableStateFlow(
        UsuarioUiState(
            nombre = "Test Preview",
            correo = "test@example.com",
            clave = "12345678",
            direccion = "Calle Preview 123",
            aceptaTerminos = true
        )
    )
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ResumenScreenPreview() {
    val mockDao = MockUsuarioDao()
    val mockRepository = MockUsuarioRepository(mockDao)

    val previewViewModel = PreviewUsuarioViewModel(mockRepository)

    Proyecto_bmiTheme {
        ResumenScreen(navController = rememberNavController(), viewModel = previewViewModel)
    }
}