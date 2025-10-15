package com.example.proyecto_bmi.ui.screens.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

data class CategoryItemUi(val nombre: String, val icon: ImageVector, val color: Color)
data class ManualUi(val title: String, val subtitle: String, val isPremium: Boolean = false)

val mockCategories = listOf(
    CategoryItemUi("Indicadores de Peso", Icons.Filled.Square, Color(0xFF1976D2)),
    CategoryItemUi("Celdas Físicas", Icons.Filled.Star, Color(0xFF1976D2)),
    CategoryItemUi("Transmisores", Icons.Filled.Build, Color(0xFF1976D2)),
    CategoryItemUi("Software", Icons.Filled.MenuBook, Color(0xFF1976D2)),
    CategoryItemUi("Calibración", Icons.Filled.Settings, Color(0xFF1976D2)),
    CategoryItemUi("Accesorios", Icons.Filled.Lightbulb, Color(0xFF1976D2))
)

val mockManuals = listOf(
    ManualUi("Manual Indididor XYZ", "Mettler Toledo"),
    ManualUi("Manual Indididor XYZ", "Mettler Toledo"),
    ManualUi("Manual Indididor XYZ", "Mettler Toledo"),
    ManualUi("Manual Indididor XYZ", "Mettler Toledo", isPremium = true),
    ManualUi("Manual Indididor XYZ", "Mettler Toledo", isPremium = true),
    ManualUi("Manual Indididor XYZ", "Mettler Toledo", isPremium = true)
)

@Composable
fun SearchComponent(busqueda: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = busqueda,
        onValueChange = onValueChange,
        label = { Text("Buscar manuales...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ManualCardMini(manual: ManualUi) {
    Card(
        modifier = Modifier.size(width = 120.dp, height = 150.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(Icons.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
            Text(manual.title, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
            Text(manual.subtitle, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
            if (manual.isPremium) {
                Text("Premium", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFE3F2FD)).padding(horizontal = 4.dp))
            }
        }
    }
}

@Composable
fun CategoryGrid(categories: List<CategoryItemUi>) {
    Text("Categorías", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp).fillMaxWidth())
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        content = {
            items(categories) { item ->
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE3F2FD),
                    ) {
                        Icon(
                            item.icon,
                            contentDescription = item.nombre,
                            modifier = Modifier.padding(16.dp).size(32.dp),
                            tint = item.color
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(item.nombre, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                }
            }
        },
        modifier = Modifier.height(250.dp)
    )
}


@Composable
fun PremiumContent(nombre: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "¡Hola, Suscriptor Premium!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Spacer(Modifier.height(8.dp))
            Text("Acceso Ilimitado a toda la biblioteca técnica.", color = Color.White)
        }
    }
    Spacer(Modifier.height(24.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Star, contentDescription = "Destacados", tint = Color(0xFFFFA000))
        Text("Manuales Destacados", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(start = 8.dp))
    }
    Spacer(Modifier.height(16.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        mockManuals.filter { it.isPremium }.take(3).forEach { manual ->
            ManualCardMini(manual.copy(isPremium = false))
        }
    }

    CategoryGrid(mockCategories)
}

@Composable
fun EstandarContent() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Desbloquea el acceso ilimitado: Hazte Suscriptor",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = { /* Navegar a planes de suscripción */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Ver Planes de Suscripción")
            }
        }
    }
    Spacer(Modifier.height(24.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Star, contentDescription = "Destacados", tint = Color(0xFFFFA000))
        Text("Manuales Destacados", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(start = 8.dp))
    }
    Spacer(Modifier.height(16.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        mockManuals.take(3).forEach { manual ->
            ManualCardMini(manual.copy(isPremium = manual.isPremium || !manual.isPremium))
        }
    }

    CategoryGrid(mockCategories)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.estado.collectAsState()
    val tipoUsuario = if (estado.nombre == "SimuladorPremium") UsuarioEntity.TIPO_PREMIUM else UsuarioEntity.TIPO_ESTANDAR
    var busqueda by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscador de Manuales de Indicadores de Pesaje") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            SearchComponent(busqueda = busqueda, onValueChange = { busqueda = it })
            Spacer(Modifier.height(16.dp))

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
            Spacer(Modifier.height(16.dp))
        }
    }
}


class MockCatalogoUsuarioRepository(usuarioDao: UsuarioDao) : UsuarioRepository(usuarioDao)

class PreviewCatalogoViewModel(repository: UsuarioRepository, isPremium: Boolean) : UsuarioViewModel(repository) {
    override val estado: StateFlow<UsuarioUiState> = MutableStateFlow(
        UsuarioUiState(
            nombre = if (isPremium) "SimuladorPremium" else "UsuarioEstandar",
            correo = "test@example.com",
            clave = "12345678",
            confirmClave = "12345678",
            direccion = "Calle Preview 123",
            aceptaTerminos = true
        )
    )
}

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