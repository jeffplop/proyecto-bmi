package com.example.proyecto_bmi.ui.screens.manuales

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val context = LocalContext.current
    LaunchedEffect(manualId) {
        manualId?.toIntOrNull()?.let { id ->
            viewModel.getPostById(id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Manual", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2563EB))
            )
        }
    ) { paddingValues ->
        if (post == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8FAFC))
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = "https://via.placeholder.com/400x300.png?text=Manual"),
                    contentDescription = post!!.title,
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                    Text(
                        text = post!!.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(Modifier.height(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = post!!.fabricante ?: "Genérico",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color(0xFF2563EB),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = post!!.body,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF64748B)
                    )

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (!post!!.pdfUrl.isNullOrBlank()) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post!!.pdfUrl))
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !post!!.pdfUrl.isNullOrBlank()
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Descargar PDF")
                    }
                }
            }
        }
    }
}