package com.example.proyecto_bmi.ui.screens.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.proyecto_bmi.data.remote.model.CategoryRemote
import com.example.proyecto_bmi.domain.model.UsuarioUiState
import com.example.proyecto_bmi.navigation.AppScreens
import com.example.proyecto_bmi.viewmodel.CatalogoViewModel
import com.example.proyecto_bmi.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

@Composable
fun CategoryCardRemote(category: CategoryRemote, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable {
                navController.navigate(AppScreens.CategoriaScreen.route + "/${category.id}")
            },
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
                modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Category, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(12.dp))
            Text(text = category.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center)
            Text(text = category.descripcion, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center, maxLines = 2)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    navController: NavController,
    userViewModel: UsuarioViewModel,
    catalogoViewModel: CatalogoViewModel = viewModel()
) {
    val userState by userViewModel.estado.collectAsState()
    val categories by catalogoViewModel.categories.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader(estado = userState)
                DrawerBody(navController) { scope.launch { drawerState.close() } }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Catálogo Dinámico") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5)),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
                    .padding(16.dp)
            ) {
                Text("Categorías Disponibles", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))

                if (categories.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(categories) { category ->
                            CategoryCardRemote(category, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerHeader(estado: UsuarioUiState) {
    Box(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(vertical = 24.dp, horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.MenuBook, null, modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.White).padding(8.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = estado.nombre.ifEmpty { "Invitado" }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Buscador B.M.I.", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun DrawerBody(navController: NavController, onDestinationClicked: () -> Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val menuItems = listOf(
        AppScreens.CatalogoScreen, AppScreens.PostScreen, AppScreens.FavoritosScreen, AppScreens.PerfilScreen, AppScreens.ContactScreen
    )
    Column(Modifier.padding(8.dp)) {
        menuItems.forEach { screen ->
            NavigationDrawerItem(
                label = { Text(screen.title) },
                icon = { Icon(screen.icon, null) },
                selected = currentRoute == screen.route,
                onClick = { navController.navigate(screen.route); onDestinationClicked() },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        NavigationDrawerItem(
            label = { Text("Cerrar Sesión") },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, null) },
            selected = false,
            onClick = { navController.navigate(AppScreens.HomeScreen.route); onDestinationClicked() }
        )
    }
}