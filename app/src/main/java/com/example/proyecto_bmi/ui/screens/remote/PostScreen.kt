package com.example.proyecto_bmi.ui.screens.remote

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto_bmi.navigation.AppScreens
import com.example.proyecto_bmi.viewmodel.PostViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(navController: NavController, viewModel: PostViewModel) {
    val posts by viewModel.postList.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val favoritesIds by viewModel.favoritesIds.collectAsState()
    val favoriteMessage by viewModel.favoriteMessage.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(favoriteMessage) {
        favoriteMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    val filteredPosts = posts.filter {
        it.title.contains(searchQuery, ignoreCase = true)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                PostDrawerHeader(nombre = if (userRole == "PREMIUM") "Socio Premium" else "Usuario Estándar")
                PostDrawerBody(navController) { scope.launch { drawerState.close() } }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Manuales Online", fontWeight = FontWeight.Bold, color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2563EB)),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, null, tint = Color.White)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color(0xFFF0F2F5))
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar manual por nombre...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF2563EB)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF2563EB)
                    )
                )

                Spacer(Modifier.height(24.dp))

                if (filteredPosts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se encontraron manuales.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredPosts) { post ->
                            val isLocked = post.isPremium && userRole != "PREMIUM"
                            val isFavorite = favoritesIds.contains(post.id)

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            post.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color(0xFF1E293B),
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (post.isPremium) {
                                            Spacer(Modifier.width(4.dp))
                                            Icon(
                                                if (isLocked) Icons.Default.Lock else Icons.Default.Star,
                                                null,
                                                tint = if (isLocked) Color(0xFFEF4444) else Color(0xFFFFD700)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(8.dp))
                                    Text(post.body, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF64748B))
                                    Spacer(Modifier.height(20.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                if (!isLocked && !post.pdfUrl.isNullOrBlank()) {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.pdfUrl))
                                                    context.startActivity(intent)
                                                }
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isLocked) Color(0xFF94A3B8) else Color(0xFF2563EB)
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            enabled = !isLocked
                                        ) {
                                            Icon(
                                                if (isLocked) Icons.Default.Lock else Icons.Default.Download,
                                                null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(if (isLocked) "Suscribirse" else "Descargar")
                                        }

                                        OutlinedButton(
                                            onClick = { viewModel.toggleFavorite(post) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = if (isFavorite) Color(0xFFEF4444) else Color(0xFF2563EB)
                                            ),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isFavorite) Color(0xFFEF4444) else Color(0xFF2563EB)
                                            )
                                        ) {
                                            Icon(
                                                if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                                                null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(if (isFavorite) "Quitar" else "Favoritos")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PostDrawerHeader(nombre: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(colors = listOf(Color(0xFF2563EB), Color(0xFF1E40AF))))
            .padding(vertical = 40.dp, horizontal = 24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.MenuBook, null, modifier = Modifier.size(40.dp), tint = Color.White)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = "Hola, $nombre", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = "Buscador B.M.I.", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun PostDrawerBody(navController: NavController, onDestinationClicked: () -> Unit) {
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
            label = { Text("Cerrar Sesión", color = Color(0xFFEF4444)) },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color(0xFFEF4444)) },
            selected = false,
            onClick = { navController.navigate(AppScreens.HomeScreen.route) { popUpTo(0) }; onDestinationClicked() }
        )
    }
}