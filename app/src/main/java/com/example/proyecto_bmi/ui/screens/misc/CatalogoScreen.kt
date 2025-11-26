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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto_bmi.data.remote.model.CategoryRemote
import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.domain.model.UsuarioUiState
import com.example.proyecto_bmi.navigation.AppScreens
import com.example.proyecto_bmi.ui.theme.Proyecto_bmiTheme
import com.example.proyecto_bmi.viewmodel.CatalogoViewModel
import com.example.proyecto_bmi.viewmodel.PostViewModel
import com.example.proyecto_bmi.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

@Composable
fun CategoryChip(category: CategoryRemote, navController: NavController) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp)
            .clickable { navController.navigate(AppScreens.CategoriaScreen.route + "/${category.id}") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.Category, null, tint = Color(0xFF2563EB))
            Spacer(Modifier.height(8.dp))
            Text(category.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
        }
    }
}

@Composable
fun ManualDestacadoCard(post: Post, navController: NavController) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(140.dp)
            .clickable {
                navController.navigate(AppScreens.ManualScreen.route + "/${post.id}")
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(post.title, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 2)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Destacado", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    navController: NavController,
    userViewModel: UsuarioViewModel,
    catalogoViewModel: CatalogoViewModel = viewModel(),
    postViewModel: PostViewModel
) {
    val userState by userViewModel.estado.collectAsState()
    val categories by catalogoViewModel.categories.collectAsState()
    val posts by postViewModel.postList.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        postViewModel.fetchPosts()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                CatalogoDrawerHeader(userState.nombre)
                CatalogoDrawerBody(navController) { scope.launch { drawerState.close() } }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Catálogo", fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, null) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8FAFC))
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8FAFC))
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar manuales...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )

                Spacer(Modifier.height(24.dp))

                if (searchQuery.isNotEmpty()) {
                    val filtered = posts.filter { it.title.contains(searchQuery, true) }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filtered) { post ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { navController.navigate(AppScreens.ManualScreen.route + "/${post.id}") },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.AutoMirrored.Filled.MenuBook, null, tint = Color(0xFF2563EB))
                                    Spacer(Modifier.width(12.dp))
                                    Text(post.title, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                } else {
                    Text("Destacados", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                    Spacer(Modifier.height(12.dp))

                    if (posts.isEmpty()) {
                        Text("Cargando destacados...", color = Color.Gray)
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(posts.take(3)) { post ->
                                ManualDestacadoCard(post, navController)
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    Text("Categorías", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                    Spacer(Modifier.height(12.dp))

                    if (categories.isEmpty()) {
                        Text("Cargando categorías...", color = Color.Gray)
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(categories) { category ->
                                CategoryChip(category, navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CatalogoDrawerHeader(nombre: String) {
    Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF2563EB)).padding(vertical = 32.dp, horizontal = 24.dp)) {
        Text("Hola, ${nombre.ifEmpty { "Usuario" }}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CatalogoDrawerBody(navController: NavController, onDestinationClicked: () -> Unit) {
    val menuItems = listOf(
        Triple("Catálogo", Icons.AutoMirrored.Filled.MenuBook, AppScreens.CatalogoScreen.route),
        Triple("Manuales Online", Icons.Filled.Wifi, AppScreens.PostScreen.route),
        Triple("Favoritos", Icons.Filled.Favorite, AppScreens.FavoritosScreen.route),
        Triple("Mi Perfil", Icons.Filled.Person, AppScreens.PerfilScreen.route),
        Triple("Contacto", Icons.Filled.SupportAgent, AppScreens.ContactScreen.route)
    )
    Column(Modifier.padding(16.dp)) {
        menuItems.forEach { (title, icon, route) ->
            NavigationDrawerItem(
                label = { Text(title) }, icon = { Icon(icon, null) }, selected = false,
                onClick = { navController.navigate(route); onDestinationClicked() },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        NavigationDrawerItem(
            label = { Text("Cerrar Sesión", color = Color.Red) },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.Red) },
            selected = false,
            onClick = { navController.navigate(AppScreens.HomeScreen.route) { popUpTo(0) }; onDestinationClicked() }
        )
    }
}