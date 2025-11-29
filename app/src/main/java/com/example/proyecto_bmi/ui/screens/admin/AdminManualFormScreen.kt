package com.example.proyecto_bmi.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.ui.screens.remote.ManualItemCard
import com.example.proyecto_bmi.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManualFormScreen(
    navController: NavController,
    viewModel: PostViewModel,
    manualId: Int?
) {
    val postToEdit by viewModel.postToEdit.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val focusManager = LocalFocusManager.current

    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var fabricante by remember { mutableStateOf("") }
    var pdfUrl by remember { mutableStateOf("") }
    var version by remember { mutableStateOf("1.0") }
    var isPremium by remember { mutableStateOf(false) }
    var categoryId by remember { mutableStateOf("1") }

    LaunchedEffect(manualId) {
        viewModel.preparePostForEditing(manualId)
    }

    LaunchedEffect(postToEdit) {
        postToEdit?.let {
            title = it.title
            body = it.body
            fabricante = it.fabricante ?: ""
            pdfUrl = it.pdfUrl ?: ""
            version = it.version ?: "1.0"
            isPremium = it.isPremium
            categoryId = it.categoryId?.toString() ?: "1"
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            viewModel.resetSaveStatus()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (manualId != null) "Editar Manual" else "Nuevo Manual", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF1F5F9))
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Visibility, null, tint = Color(0xFF0F172A))
                        Spacer(Modifier.width(8.dp))
                        Text("Vista Previa", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    }
                    Spacer(Modifier.height(8.dp))

                    val previewPost = Post(
                        userId = 0,
                        id = 0,
                        title = title.ifBlank { "Título..." },
                        body = body.ifBlank { "Descripción..." },
                        fabricante = fabricante.ifBlank { "Marca" },
                        pdfUrl = pdfUrl,
                        version = version,
                        isPremium = isPremium,
                        categoryId = categoryId.toIntOrNull() ?: 1
                    )

                    ManualItemCard(
                        post = previewPost,
                        isLocked = false, isFavorite = false, isAdmin = false,
                        onFavoriteClick = {}, onDownloadClick = {}, onEditClick = {}, onDeleteClick = {}
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = title, onValueChange = { title = it },
                        label = { Text("Título Principal") }, modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFF59E0B))
                    )
                    OutlinedTextField(
                        value = fabricante, onValueChange = { fabricante = it },
                        label = { Text("Fabricante") }, modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFF59E0B))
                    )
                    OutlinedTextField(
                        value = body, onValueChange = { body = it },
                        label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFF59E0B))
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = version, onValueChange = { version = it },
                            label = { Text("Versión") }, modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFF59E0B))
                        )
                        OutlinedTextField(
                            value = categoryId, onValueChange = { if (it.all { char -> char.isDigit() }) categoryId = it },
                            label = { Text("ID Cat.") }, modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFF59E0B))
                        )
                    }

                    OutlinedTextField(
                        value = pdfUrl, onValueChange = { pdfUrl = it },
                        label = { Text("URL del PDF") }, modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFF59E0B))
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isPremium, onCheckedChange = { isPremium = it }, colors = CheckboxDefaults.colors(checkedColor = Color(0xFFF59E0B)))
                        Text("Es Contenido Premium", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Button(
                onClick = {
                    focusManager.clearFocus()

                    val finalId: Int? = manualId

                    val newPost = Post(
                        userId = 0,
                        id = finalId,
                        title = title,
                        body = body,
                        fabricante = fabricante,
                        pdfUrl = pdfUrl,
                        version = version,
                        isPremium = isPremium,
                        categoryId = categoryId.toIntOrNull() ?: 1
                    )

                    viewModel.saveOrUpdatePost(newPost, manualId != null)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                enabled = !isLoading && title.isNotBlank() && body.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Save, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar Cambios", fontSize = 16.sp)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}