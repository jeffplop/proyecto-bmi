package com.example.proyecto_bmi.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController, viewModel: AdminViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedPost by remember { mutableStateOf<Post?>(null) }
    val context = LocalContext.current

    LaunchedEffect(state.operationSuccess) {
        state.operationSuccess?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            showDialog = false
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(state.operationError) {
        state.operationError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administración Manuales", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2563EB))
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { selectedPost = null; showDialog = true }, containerColor = Color(0xFF2563EB)) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.posts) { post ->
                        AdminPostItem(
                            post = post,
                            onEdit = { selectedPost = it; showDialog = true },
                            onDelete = { viewModel.deletePost(it.id) }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        PostDialog(
            post = selectedPost,
            onDismiss = { showDialog = false },
            onConfirm = { post -> viewModel.savePost(post, selectedPost != null) }
        )
    }
}

@Composable
fun AdminPostItem(post: Post, onEdit: (Post) -> Unit, onDelete: (Post) -> Unit) {
    Card(elevation = CardDefaults.cardElevation(4.dp), shape = RoundedCornerShape(8.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(post.title, fontWeight = FontWeight.Bold)
                Text("ID: ${post.id} | Fab: ${post.fabricante}", style = MaterialTheme.typography.bodySmall)
            }
            Row {
                IconButton(onClick = { onEdit(post) }) { Icon(Icons.Default.Edit, null, tint = Color.Blue) }
                IconButton(onClick = { onDelete(post) }) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
            }
        }
    }
}

@Composable
fun PostDialog(post: Post?, onDismiss: () -> Unit, onConfirm: (Post) -> Unit) {
    var title by remember { mutableStateOf(post?.title ?: "") }
    var body by remember { mutableStateOf(post?.body ?: "") }
    var fabricante by remember { mutableStateOf(post?.fabricante ?: "") }
    var isPremium by remember { mutableStateOf(post?.isPremium ?: false) }
    var categoryId by remember { mutableStateOf(post?.categoryId?.toString() ?: "1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (post == null) "Nuevo Manual" else "Editar Manual") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = fabricante, onValueChange = { fabricante = it }, label = { Text("Fabricante") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = categoryId, onValueChange = { categoryId = it }, label = { Text("ID Categoría") }, modifier = Modifier.fillMaxWidth())
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isPremium, onCheckedChange = { isPremium = it })
                    Text("Es Premium")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val newPost = Post(
                    id = post?.id ?: 0,
                    userId = 1,
                    title = title,
                    body = body,
                    fabricante = fabricante,
                    isPremium = isPremium,
                    categoryId = categoryId.toIntOrNull() ?: 1
                )
                onConfirm(newPost)
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}