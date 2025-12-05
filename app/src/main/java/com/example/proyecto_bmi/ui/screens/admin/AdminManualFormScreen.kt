package com.example.proyecto_bmi.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManualFormScreen(
    navController: NavController,
    viewModel: PostViewModel,
    manualId: String? = null
) {
    val selectedPost by viewModel.selectedPost.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val context = LocalContext.current
    val isEditMode = manualId != null

    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var fabricante by remember { mutableStateOf("") }
    var version by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var pdfUrl by remember { mutableStateOf("") }
    var isPremium by remember { mutableStateOf(false) }

    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedCategoryName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchCategories()
        if (isEditMode) {
            manualId?.toIntOrNull()?.let { viewModel.getPostById(it) }
        } else {
            viewModel.resetSaveStatus()
        }
    }

    LaunchedEffect(selectedPost) {
        selectedPost?.let { post ->
            if (isEditMode) {
                title = post.title
                body = post.body
                fabricante = post.fabricante ?: ""
                version = post.version ?: ""
                fecha = post.fecha ?: ""
                pdfUrl = post.pdfUrl ?: ""
                isPremium = post.isPremium
                selectedCategoryId = post.categoryId
            }
        }
    }

    LaunchedEffect(categories, selectedCategoryId) {
        if (selectedCategoryId != null && categories.isNotEmpty()) {
            val cat = categories.find { it.id == selectedCategoryId }
            selectedCategoryName = cat?.nombre ?: "Seleccionar Categoría"
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            Toast.makeText(context, "Operación exitosa", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            viewModel.resetSaveStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Manual" else "Nuevo Manual", color = Color.White) },
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Información General", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título del Manual") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = body,
                        onValueChange = { body = it },
                        label = { Text("Descripción / Cuerpo") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )

                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedCategoryName.ifEmpty { "Seleccionar Categoría" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            if (categories.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Cargando categorías...") },
                                    onClick = { }
                                )
                            } else {
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category.nombre) },
                                        onClick = {
                                            selectedCategoryId = category.id
                                            selectedCategoryName = category.nombre
                                            categoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Detalles Técnicos", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = fabricante,
                            onValueChange = { fabricante = it },
                            label = { Text("Fabricante") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = version,
                            onValueChange = { version = it },
                            label = { Text("Versión") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = fecha,
                        onValueChange = { fecha = it },
                        label = { Text("Año / Fecha") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = pdfUrl,
                        onValueChange = { pdfUrl = it },
                        label = { Text("URL del PDF") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("https://ejemplo.com/manual.pdf") }
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Contenido Premium", fontWeight = FontWeight.Bold)
                        Text("Solo visible para suscriptores", fontSize = 12.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = isPremium,
                        onCheckedChange = { isPremium = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFF59E0B), checkedTrackColor = Color(0xFFFEF3C7))
                    )
                }
            }

            Button(
                onClick = {
                    if (title.isNotBlank() && body.isNotBlank() && selectedCategoryId != null) {
                        val post = Post(
                            id = if (isEditMode) manualId?.toIntOrNull() else null,
                            userId = 0,
                            title = title,
                            body = body,
                            fabricante = fabricante,
                            version = version,
                            fecha = fecha,
                            pdfUrl = pdfUrl,
                            isPremium = isPremium,
                            categoryId = selectedCategoryId
                        )
                        viewModel.saveOrUpdatePost(post, isEditMode)
                    } else {
                        Toast.makeText(context, "Título, descripción y categoría son obligatorios", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Check, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar Manual")
                }
            }
        }
    }
}