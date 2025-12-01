package com.example.proyecto_bmi.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto_bmi.data.remote.model.CategoryRemote
import com.example.proyecto_bmi.viewmodel.CatalogoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoryScreen(navController: NavController, viewModel: CatalogoViewModel) {
    val categories by viewModel.categories.collectAsState()
    val operationMessage by viewModel.operationMessage.collectAsState()

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var currentCategory by remember { mutableStateOf<CategoryRemote?>(null) }

    LaunchedEffect(operationMessage) {
        operationMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Categorías", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    currentCategory = null
                    showDialog = true
                },
                containerColor = Color(0xFFF59E0B)
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF1F5F9))
                .padding(16.dp)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categories) { category ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(category.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(category.descripcion, fontSize = 14.sp, color = Color.Gray)
                            }
                            Row {
                                IconButton(onClick = {
                                    currentCategory = category
                                    showDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, null, tint = Color(0xFF2563EB))
                                }
                                IconButton(onClick = { viewModel.deleteCategory(category.id) }) {
                                    Icon(Icons.Default.Delete, null, tint = Color(0xFFEF4444))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        CategoryDialog(
            category = currentCategory,
            onDismiss = { showDialog = false },
            onConfirm = { name, desc ->
                val newCat = CategoryRemote(
                    id = currentCategory?.id ?: 0,
                    nombre = name,
                    descripcion = desc
                )
                viewModel.saveCategory(newCat)
                showDialog = false
            }
        )
    }
}

@Composable
fun CategoryDialog(
    category: CategoryRemote?,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(category?.nombre ?: "") }
    var desc by remember { mutableStateOf(category?.descripcion ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (category == null) "Nueva Categoría" else "Editar Categoría") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") }
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descripción") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, desc) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}