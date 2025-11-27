package com.example.proyecto_bmi.ui.screens.manuales

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.proyecto_bmi.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualScreen(
    navController: NavController,
    manualId: String?,
    viewModel: PostViewModel = viewModel()
) {
    val post by viewModel.selectedPost.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(manualId) {
        manualId?.toIntOrNull()?.let { id ->
            viewModel.getPostById(id)
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color(0xFF1E293B))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2563EB))
                }
            }
            else if (errorMessage != null || post == null) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Error, null, tint = Color(0xFFEF4444), modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("¡Ups! Algo salió mal.", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(errorMessage ?: "No se encontró el manual.", color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { manualId?.toIntOrNull()?.let { viewModel.getPostById(it) } },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Text("Reintentar")
                    }
                }
            }
            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = "https://via.placeholder.com/600x400.png?text=Manual"),
                            contentDescription = "Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))))
                        )
                        Column(
                            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
                        ) {
                            Text(
                                text = post!!.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = post!!.fabricante ?: "Genérico",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Especificaciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Spacer(Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                InfoChip(icon = Icons.Default.History, label = "Versión", value = post!!.version ?: "1.0")
                                InfoChip(icon = Icons.Default.CalendarToday, label = "Fecha", value = post!!.fecha ?: "N/A")
                                InfoChip(icon = Icons.Default.Business, label = "Marca", value = post!!.fabricante ?: "N/A")
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Text("Descripción", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Spacer(Modifier.height(8.dp))
                        Text(text = post!!.body, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF64748B), lineHeight = 24.sp)
                    }

                    Spacer(Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { /* Leer Online */ },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2563EB))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.MenuBook, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Leer Online")
                        }

                        Button(
                            onClick = {
                                if (!post!!.pdfUrl.isNullOrBlank()) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post!!.pdfUrl))
                                    context.startActivity(intent)
                                }
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            enabled = !post!!.pdfUrl.isNullOrBlank()
                        ) {
                            Icon(Icons.Default.Download, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Descargar")
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun InfoChip(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFEFF6FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color(0xFF94A3B8))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
    }
}