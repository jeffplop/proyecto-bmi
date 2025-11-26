package com.example.proyecto_bmi.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
                title = { Text("Resumen de Registro", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al inicio",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2563EB))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2563EB), Color(0xFF1E40AF))
                    )
                )
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Éxito",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "¡Registro Exitoso!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Text(
                        text = "Tu cuenta ha sido creada.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF64748B)
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

                    InfoRow(icon = Icons.Filled.Person, label = "Nombre", value = estado.nombre)
                    InfoRow(icon = Icons.Filled.Email, label = "Correo", value = estado.correo)
                    InfoRow(icon = Icons.Filled.Phone, label = "Teléfono", value = estado.telefono)
                    InfoRow(icon = Icons.Filled.Lock, label = "Contraseña", value = "∗".repeat(estado.clave.length))

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = { navController.navigate("catalogo") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Text("Ir al Catálogo")
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { navController.navigate("contact") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Contactar a Soporte")
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF2563EB),
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
        }
    }
}

class MockUsuarioDao : UsuarioDao {
    override suspend fun insertUser(user: UsuarioEntity): Long = 1L
    override suspend fun updateUser(user: UsuarioEntity) {}
    override suspend fun getUserByCredentials(email: String, clave: String): UsuarioEntity? = null
    override suspend fun getUserById(userId: Int): UsuarioEntity? = null
    override suspend fun updateTipoUsuario(userId: Int, nuevoTipo: String) {}
    override suspend fun count(): Int = 0
    override suspend fun getAllUsers(): List<UsuarioEntity> = emptyList()
}

class MockUsuarioRepository(usuarioDao: UsuarioDao) : UsuarioRepository(usuarioDao)

class PreviewAuthViewModel(repository: UsuarioRepository) : UsuarioViewModel(repository) {
    override val estado: StateFlow<UsuarioUiState> = MutableStateFlow(
        UsuarioUiState(
            nombre = "Test Preview",
            correo = "test@example.com",
            clave = "12345678",
            confirmClave = "12345678",
            telefono = "+56912345678",
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