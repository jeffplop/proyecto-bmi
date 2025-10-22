package com.example.proyecto_bmi.ui.screens.categorias

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_bmi.domain.model.ProductoCategoria
import com.example.proyecto_bmi.ui.theme.Proyecto_bmiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaScreen(navController: NavController, categoriaId: String?) {
    val (titulo, productos) = when (categoriaId) {
        "indicadores" -> "Indicadores" to listOf(
            ProductoCategoria("Indicador Digital", "ID-200"),
            ProductoCategoria("Indicador de Proceso", "IP-5000"),
            ProductoCategoria("Indicador Básico", "IB-101")
        )
        "impresoras" -> "Impresoras" to listOf(
            ProductoCategoria("Impresora Térmica", "IT-80"),
            ProductoCategoria("Impresora de Etiquetas", "IE-Zebra"),
            ProductoCategoria("Impresora Portátil", "IP-M1")
        )
        "bluetooth" -> "Bluetooth" to listOf(
            ProductoCategoria("Módulo Bluetooth", "BT-5.0"),
            ProductoCategoria("Adaptador Serial", "AS-BT"),
            ProductoCategoria("Kit de Conexión", "KC-Wireless")
        )
        else -> "Categoría no encontrada" to emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(productos) { producto ->
                ProductoCard(producto)
            }
        }
    }
}

@Composable
fun ProductoCard(producto: ProductoCategoria) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { /* TODO: Acción al pulsar producto */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text(
                text = producto.nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = producto.modelo,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriaScreenPreview() {
    Proyecto_bmiTheme {
        CategoriaScreen(navController = rememberNavController(), categoriaId = "indicadores")
    }
}