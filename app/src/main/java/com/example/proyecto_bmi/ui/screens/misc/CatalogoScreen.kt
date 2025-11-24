package com.example.proyecto_bmi.ui.screens.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_bmi.data.local.dao.UsuarioDao
import com.example.proyecto_bmi.data.local.entity.UsuarioEntity
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import com.example.proyecto_bmi.domain.model.CategoriaUI
import com.example.proyecto_bmi.domain.model.ManualUI
import com.example.proyecto_bmi.domain.model.UsuarioUiState
import com.example.proyecto_bmi.navigation.AppScreens
import com.example.proyecto_bmi.ui.theme.Proyecto_bmiTheme
import com.example.proyecto_bmi.viewmodel.UsuarioViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

val mockManuals = listOf(
    ManualUI("LP7516B", "Manual del Indicador", "LP7516B"),
    ManualUI("EXCELL", "Manual del Indicador", "EXCELL"),
    ManualUI("LP7516", "Manual del Indicador", "LP7516", isPremium = true)
)

val mockCategories = listOf(
    CategoriaUI("indicadores", "Indicadores", Icons.Filled.Square),
    CategoriaUI("impresoras", "Impresoras", Icons.Filled.Print),
    CategoriaUI("bluetooth", "Bluetooth", Icons.Filled.Bluetooth)
)

@Composable
fun SearchComponent(busqueda: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = busqueda,
        onValueChange = onValueChange,
        label = { Text("Buscar manuales por modelo, marca...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(32.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.LightGray
        )
    )
}

@Composable
fun ManualCard(manual: ManualUI, navController: NavController, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.width(160.dp).height(200.dp).clickable { navController.navigate(AppScreens.ManualScreen.route + "/${manual.id}") },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = manual.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface)
                Text(text = manual.subtitle, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
            }
            if (manual.isPremium) {
                Badge(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White) {
                    Text("Premium", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp)
                }
            } else {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun CategoryCard(category: CategoriaUI, navController: NavController, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.width(160.dp).height(200.dp).clickable { navController.navigate(AppScreens.CategoriaScreen.route + "/${category.id}") },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(category.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(text = category.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun PremiumContent(nombre: String, navController: NavController) {
    Column {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.WorkspacePremium, contentDescription = "Premium", tint = Color.White, modifier = Modifier.size(40.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(text = "¡Hola, ${nombre}!", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Text("Acceso total a la biblioteca técnica.", color = Color.White.copy(alpha = 0.8f))
                }
            }
        }
        SectionTitle(title = "Manuales Populares")
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(mockManuals) { manual -> ManualCard(manual, navController) }
        }
        SectionTitle(title = "Categorías")
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(mockCategories) { category -> CategoryCard(category, navController) }
        }
    }
}

@Composable
fun EstandarContent(navController: NavController) {
    Column {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D47A1))
        ) {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Star, contentDescription = "Premium", tint = Color(0xFFFFD700), modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(8.dp))
                Text(text = "Desbloquea el Acceso Ilimitado", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Color.White, textAlign = TextAlign.Center)
                Text("Conviértete en Premium para ver todo el contenido.", color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                Spacer(Modifier.height(16.dp))
                Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF0D47A1))) {
                    Text("Ver Planes de Suscripción")
                }
            }
        }
        SectionTitle(title = "Manuales Populares")
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(mockManuals) { manual -> ManualCard(manual, navController) }
        }
        SectionTitle(title = "Categorías")
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(mockCategories) { category -> CategoryCard(category, navController) }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp)
    )
}

@Composable
fun DrawerHeader(estado: UsuarioUiState) {
    Box(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(vertical = 24.dp, horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = "App Icon",
                modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.White).padding(8.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = estado.nombre.ifEmpty { "Invitado" }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Buscador de Manuales", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun DrawerBody(navController: NavController, onDestinationClicked: () -> Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val menuItems = listOf(
        AppScreens.CatalogoScreen,
        AppScreens.FavoritosScreen,
        AppScreens.PerfilScreen,
        AppScreens.PostScreen,
        AppScreens.ContactScreen
    )

    Column(Modifier.padding(8.dp)) {
        menuItems.forEach { screen ->
            NavigationDrawerItem(
                label = { Text(screen.title) },
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) { launchSingleTop = true }
                    onDestinationClicked()
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = DividerDefaults.Thickness, color = DividerDefaults.color)
        NavigationDrawerItem(
            label = { Text("Cerrar Sesión") },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar Sesión") },
            selected = false,
            onClick = {
                navController.navigate(AppScreens.HomeScreen.route) {
                    popUpTo(AppScreens.HomeScreen.route) { inclusive = true }
                }
                onDestinationClicked()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.estado.collectAsState()
    val tipoUsuario = if (estado.nombre == "SimuladorPremium") UsuarioEntity.TIPO_PREMIUM else UsuarioEntity.TIPO_ESTANDAR
    var busqueda by remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader(estado = estado)
                DrawerBody(navController = navController) {
                    scope.launch { drawerState.close() }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Catálogo General") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5), titleContentColor = MaterialTheme.colorScheme.onSurface),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir Menú")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF5F5F5))) {
                item {
                    Spacer(Modifier.height(16.dp))
                    SearchComponent(busqueda = busqueda, onValueChange = { busqueda = it })
                }
                item {
                    if (tipoUsuario == UsuarioEntity.TIPO_PREMIUM) {
                        PremiumContent(estado.nombre, navController)
                    } else {
                        EstandarContent(navController)
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

class MockCatalogoUsuarioRepository(usuarioDao: UsuarioDao) : UsuarioRepository(usuarioDao)
class PreviewCatalogoViewModel(repository: UsuarioRepository, isPremium: Boolean) : UsuarioViewModel(repository) {
    override val estado: StateFlow<UsuarioUiState> = MutableStateFlow(
        UsuarioUiState(nombre = if (isPremium) "SimuladorPremium" else "UsuarioEstandar")
    )
}
private val catalogoPremiumViewModel: PreviewCatalogoViewModel by lazy {
    val mockDao = object : UsuarioDao {
        override suspend fun insertUser(user: UsuarioEntity): Long = 1L
        override suspend fun getUserByCredentials(email: String, clave: String): UsuarioEntity? = null
        override suspend fun getUserById(userId: Int): UsuarioEntity? = null
        override suspend fun updateTipoUsuario(userId: Int, nuevoTipo: String) {}
        override suspend fun count(): Int = 0
        override suspend fun getAllUsers(): List<UsuarioEntity> = emptyList()
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
        override suspend fun count(): Int = 0
        override suspend fun getAllUsers(): List<UsuarioEntity> = emptyList()
    }
    val mockRepository = MockCatalogoUsuarioRepository(mockDao)
    PreviewCatalogoViewModel(mockRepository, isPremium = false)
}

@Preview(showBackground = true, name = "Catálogo Estándar")
@Composable
fun CatalogoScreenEstandarPreview() {
    Proyecto_bmiTheme {
        CatalogoScreen(navController = rememberNavController(), viewModel = catalogoEstandarViewModel)
    }
}