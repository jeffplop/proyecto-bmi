package com.example.proyecto_bmi.ui.screens.remote

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.navigation.AppScreens
import com.example.proyecto_bmi.viewmodel.PostViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(navController: NavController, viewModel: PostViewModel) {
    val posts by viewModel.postList.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val favoritesIds by viewModel.favoritesIds.collectAsState()
    val operationMessage by viewModel.operationMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(operationMessage) {
        operationMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    val filteredPosts = posts.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                (it.fabricante?.contains(searchQuery, ignoreCase = true) == true)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                drawerContentColor = Color(0xFF1E293B)
            ) {
                PostDrawerHeader(role = userRole)
                PostDrawerBody(navController) { scope.launch { drawerState.close() } }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("B.M.I. Manuales", fontWeight = FontWeight.Bold, color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A)),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, null, tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refreshAllData() }) {
                            Icon(Icons.Default.Refresh, null, tint = Color.White)
                        }
                    }
                )
            },
            floatingActionButton = {
                if (userRole == "ADMIN") {
                    FloatingActionButton(
                        onClick = { navController.navigate("admin_form") },
                        containerColor = Color(0xFFF59E0B),
                        contentColor = Color(0xFF0F172A)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Crear")
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color(0xFFF1F5F9))
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar modelo, marca...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF0F172A)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFFF59E0B),
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(Modifier.height(16.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF0F172A))
                    }
                } else if (filteredPosts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay manuales disponibles.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(items = filteredPosts, key = { it.id }) { post ->
                            val isLocked = post.isPremium && (userRole != "PREMIUM" && userRole != "ADMIN")
                            val isFavorite = favoritesIds.contains(post.id)

                            ManualItemCard(
                                post = post,
                                isLocked = isLocked,
                                isFavorite = isFavorite,
                                isAdmin = userRole == "ADMIN",
                                onFavoriteClick = { viewModel.toggleFavorite(post) },
                                onDownloadClick = {
                                    if (!post.pdfUrl.isNullOrBlank()) {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.pdfUrl))
                                        context.startActivity(intent)
                                    } else {
                                        Toast.makeText(context, "URL no válida", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onEditClick = { navController.navigate("admin_form?manualId=${post.id}") },
                                onDeleteClick = { viewModel.deletePost(post.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ManualItemCard(
    post: Post,
    isLocked: Boolean,
    isFavorite: Boolean,
    isAdmin: Boolean,
    onFavoriteClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Manual") },
            text = { Text("¿Eliminar '${post.title}' permanentemente?") },
            confirmButton = {
                Button(onClick = { onDeleteClick(); showDeleteDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Eliminar") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (post.isPremium) Color(0xFFFFF7ED) else Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (post.isPremium) Icons.Default.WorkspacePremium else Icons.Default.Description,
                        null,
                        tint = if (post.isPremium) Color(0xFFF59E0B) else Color(0xFF64748B)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                    Text(post.fabricante ?: "Genérico", fontSize = 12.sp, color = Color(0xFF64748B))
                }
                if (isLocked) {
                    Icon(Icons.Default.Lock, null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(post.body, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF475569), maxLines = 2)
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onDownloadClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isLocked) Color(0xFF94A3B8) else Color(0xFF0F172A)),
                    enabled = !isLocked,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(if (isLocked) "Suscripción" else "Ver PDF")
                }

                OutlinedButton(
                    onClick = onFavoriteClick,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isFavorite) Color.Red else Color(0xFFE2E8F0)),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.width(50.dp)
                ) {
                    Icon(if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder, null, tint = if (isFavorite) Color.Red else Color(0xFF64748B))
                }
            }

            if (isAdmin) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = Color(0xFF0F172A))
                        Spacer(Modifier.width(4.dp))
                        Text("Editar", color = Color(0xFF0F172A))
                    }
                    TextButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp), tint = Color(0xFFEF4444))
                        Spacer(Modifier.width(4.dp))
                        Text("Borrar", color = Color(0xFFEF4444))
                    }
                }
            }
        }
    }
}

@Composable
private fun PostDrawerHeader(role: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A))
            .padding(vertical = 48.dp, horizontal = 24.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF59E0B)),
                contentAlignment = Alignment.Center
            ) {
                Text(role.first().toString(), fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            }
            Spacer(Modifier.height(16.dp))
            Text("Bienvenido", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(if(role == "ADMIN") "Administrador" else if(role == "PREMIUM") "Socio Premium" else "Usuario Estándar", color = Color(0xFF94A3B8), fontSize = 14.sp)
        }
    }
}

@Composable
fun PostDrawerBody(navController: NavController, closeDrawer: () -> Unit) {
    val items = listOf(
        "Inicio" to AppScreens.HomeScreen.route,
        "Catálogo" to AppScreens.CatalogoScreen.route,
        "Manuales" to AppScreens.PostScreen.route,
        "Favoritos" to AppScreens.FavoritosScreen.route,
        "Perfil" to AppScreens.PerfilScreen.route,
        "Soporte" to AppScreens.ContactScreen.route
    )

    Column(Modifier.padding(12.dp)) {
        items.forEach { (label, route) ->
            NavigationDrawerItem(
                label = { Text(label, fontWeight = FontWeight.Medium) },
                selected = false,
                onClick = { navController.navigate(route); closeDrawer() },
                modifier = Modifier.padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        NavigationDrawerItem(
            label = { Text("Cerrar Sesión", color = Color(0xFFEF4444)) },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color(0xFFEF4444)) },
            selected = false,
            onClick = { navController.navigate(AppScreens.HomeScreen.route) { popUpTo(0) }; closeDrawer() },
            shape = RoundedCornerShape(8.dp)
        )
    }
}