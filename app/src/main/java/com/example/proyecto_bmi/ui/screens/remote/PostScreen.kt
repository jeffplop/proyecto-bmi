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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto_bmi.data.local.SessionManager
import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.navigation.AppScreens
import com.example.proyecto_bmi.ui.components.AppDrawer
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
    val sessionManager = SessionManager(context)

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
            AppDrawer(
                navController = navController,
                drawerState = drawerState,
                scope = scope,
                userName = "Usuario",
                userRole = userRole,
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate(AppScreens.HomeScreen.route) {
                        popUpTo(0)
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Manuales Online", fontWeight = FontWeight.Bold, color = Color.White) },
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
                        focusedBorderColor = Color(0xFFF59E0B)
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
                        items(items = filteredPosts, key = { it.id ?: 0 }) { post ->
                            val isLocked = post.isPremium && (userRole != "PREMIUM" && userRole != "ADMIN")
                            val isFavorite = favoritesIds.contains(post.id ?: -1)

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
                                        Toast.makeText(context, "URL no disponible", Toast.LENGTH_SHORT).show()
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
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Eliminar '${post.title}'?") },
            confirmButton = {
                Button(onClick = { onDeleteClick(); showDeleteDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Eliminar") }
            },
            dismissButton = { OutlinedButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }

    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                    Text(post.fabricante ?: "Genérico", fontSize = 12.sp, color = Color(0xFF64748B))
                }
                if (isLocked) Icon(Icons.Default.Lock, null, tint = Color(0xFFEF4444))
            }
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onDownloadClick,
                    modifier = Modifier.weight(1f),
                    enabled = !isLocked,
                    colors = ButtonDefaults.buttonColors(containerColor = if(isLocked) Color.Gray else Color(0xFF0F172A))
                ) {
                    Text(if(isLocked) "Premium" else "Ver PDF")
                }
                OutlinedButton(onClick = onFavoriteClick) {
                    Icon(if(isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder, null, tint = if(isFavorite) Color.Red else Color.Gray)
                }
            }

            if (isAdmin) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onEditClick) { Text("Editar", color = Color(0xFF0F172A)) }
                    TextButton(onClick = { showDeleteDialog = true }) { Text("Borrar", color = Color(0xFFEF4444)) }
                }
            }
        }
    }
}