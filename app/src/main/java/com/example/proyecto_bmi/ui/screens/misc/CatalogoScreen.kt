package com.example.proyecto_bmi.ui.screens.misc

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_bmi.data.local.dao.UsuarioDao
import com.example.proyecto_bmi.data.local.entity.UsuarioEntity
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import com.example.proyecto_bmi.domain.model.UsuarioUiState
import com.example.proyecto_bmi.ui.theme.Proyecto_bmiTheme
import com.example.proyecto_bmi.viewmodel.UsuarioViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.estado.collectAsState()
    val tipoUsuario = if (estado.nombre == "SimuladorPremium") UsuarioEntity.TIPO_PREMIUM else UsuarioEntity.TIPO_ESTANDAR

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscador de Manuales") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (tipoUsuario == UsuarioEntity.TIPO_PREMIUM) {
                PremiumContent(estado.nombre)
            } else {
                EstandarContent()
            }
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
fun PremiumContent(nombre: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "¡Hola, Suscriptor Premium!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text("Acceso Ilimitado a toda la biblioteca técnica, incluyendo diagramas y calibración.")
        }
    }
    Spacer(Modifier.height(24.dp))
    Text("Manuales Destacados (Premium)", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(16.dp))
    Text("Manual Indicador XYZ - Serie Pro (Acceso Completo)", modifier = Modifier.fillMaxWidth().background(Color(0xFF66BB6A)).padding(8.dp), color = Color.White)
    Text("Manual Calibración Avanzada Mettler (Acceso Completo)", modifier = Modifier.fillMaxWidth().background(Color(0xFF66BB6A)).padding(8.dp), color = Color.White)
}

@Composable
fun EstandarContent() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Desbloquea el acceso ilimitado: Hazte Suscriptor",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = {  }, modifier = Modifier.fillMaxWidth()) {
                Text("Ver Planes de Suscripción")
            }
        }
    }
    Spacer(Modifier.height(24.dp))
    Text("Manuales Básicos (Gratuitos)", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(16.dp))
    Text("Manual Indicador XYZ - Ficha Técnica (Básico)", modifier = Modifier.fillMaxWidth().background(Color(0xFFB0BEC5)).padding(8.dp))
    Text("Guía de Inicio Rápido (Básico)", modifier = Modifier.fillMaxWidth().background(Color(0xFFB0BEC5)).padding(8.dp))
}

class MockCatalogoUsuarioRepository(usuarioDao: UsuarioDao) : UsuarioRepository(usuarioDao)

class PreviewCatalogoViewModel(repository: UsuarioRepository, isPremium: Boolean) : UsuarioViewModel(repository) {
    override val estado: StateFlow<UsuarioUiState> = MutableStateFlow(
        UsuarioUiState(
            nombre = if (isPremium) "SimuladorPremium" else "UsuarioEstandar",
            correo = "test@example.com",
            clave = "12345678",
            direccion = "Calle Preview 123",
            aceptaTerminos = true
        )
    )
}

// Inicialización de ViewModel Mock Premium fuera del Composable para evitar la advertencia Lint
private val catalogoPremiumViewModel: PreviewCatalogoViewModel by lazy {
    val mockDao = object : UsuarioDao {
        override suspend fun insertUser(user: UsuarioEntity): Long = 1L
        override suspend fun getUserByCredentials(email: String, clave: String): UsuarioEntity? = null
        override suspend fun getUserById(userId: Int): UsuarioEntity? = null
        override suspend fun updateTipoUsuario(userId: Int, nuevoTipo: String) {}
    }
    val mockRepository = MockCatalogoUsuarioRepository(mockDao)
    PreviewCatalogoViewModel(mockRepository, isPremium = true)
}

// Inicialización de ViewModel Mock Estándar fuera del Composable para evitar la advertencia Lint
private val catalogoEstandarViewModel: PreviewCatalogoViewModel by lazy {
    val mockDao = object : UsuarioDao {
        override suspend fun insertUser(user: UsuarioEntity): Long = 1L
        override suspend fun getUserByCredentials(email: String, clave: String): UsuarioEntity? = null
        override suspend fun getUserById(userId: Int): UsuarioEntity? = null
        override suspend fun updateTipoUsuario(userId: Int, nuevoTipo: String) {}
    }
    val mockRepository = MockCatalogoUsuarioRepository(mockDao)
    PreviewCatalogoViewModel(mockRepository, isPremium = false)
}

@Preview(showBackground = true)
@Composable
fun CatalogoScreenPremiumPreview() {
    Proyecto_bmiTheme {
        CatalogoScreen(navController = rememberNavController(), viewModel = catalogoPremiumViewModel)
    }
}

@Preview(showBackground = true)
@Composable
fun CatalogoScreenEstandarPreview() {
    Proyecto_bmiTheme {
        CatalogoScreen(navController = rememberNavController(), viewModel = catalogoEstandarViewModel)
    }
}