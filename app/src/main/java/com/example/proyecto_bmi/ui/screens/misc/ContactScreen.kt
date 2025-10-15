package com.example.proyecto_bmi.ui.screens.misc

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_bmi.ui.theme.Proyecto_bmiTheme

fun getFileName(uri: Uri, context: Context): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex != -1) {
                    result = cursor.getString(columnIndex)
                }
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1) {
            result = result?.substring(cut!! + 1)
        }
    }
    return result ?: "Archivo desconocido"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(navController: NavController) {
    var userEmail by remember { mutableStateOf("") }
    var userMessage by remember { mutableStateOf("") }
    var ubicacionStatus by remember { mutableStateOf("Ubicación no obtenida") }
    var isLocationObtained by remember { mutableStateOf(false) }
    var attachedFileUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            ubicacionStatus = "Ubicación obtenida ✓"
            isLocationObtained = true
            Toast.makeText(context, "Ubicación registrada con éxito", Toast.LENGTH_SHORT).show()
        } else {
            ubicacionStatus = "Permiso de ubicación denegado"
            isLocationObtained = false
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        attachedFileUri = uri
    }

    val isFormValid = userEmail.isNotBlank() && userMessage.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contacto y Soporte") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.primary, Color(0xFF0D47A1))))
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("Soporte Técnico BMI", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(16.dp))
                ContactInfoRow(icon = Icons.Filled.Email, label = "Correo Electrónico", value = "soporte@bmi.com")
                Spacer(Modifier.height(16.dp))
                ContactInfoRow(icon = Icons.Filled.Phone, label = "Teléfono de Soporte", value = "+56 2 2888 8888", isClickable = true, onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:+56228888888") }
                    context.startActivity(intent)
                })
                Divider(modifier = Modifier.padding(vertical = 24.dp))
                Text("Envíanos tu consulta", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Email, contentDescription = "Correo", modifier = Modifier.padding(end = 8.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = { userEmail = it },
                        label = { Text("Tu correo electrónico") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.Message, contentDescription = "Mensaje", modifier = Modifier.padding(end = 8.dp, top = 16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = userMessage,
                        onValueChange = { userMessage = it },
                        label = { Text("Describe tu problema") },
                        modifier = Modifier.weight(1f),
                        minLines = 4
                    )
                }
                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "Ubicación", modifier = Modifier.padding(end = 8.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = ubicacionStatus,
                        onValueChange = {},
                        label = { Text("Estado de la Ubicación") },
                        modifier = Modifier.weight(1f),
                        readOnly = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = if (isLocationObtained) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = if (isLocationObtained) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface,
                        )
                    )
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Adjuntar Ubicación Actual")
                }
                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AttachFile, contentDescription = "Archivo", modifier = Modifier.padding(end = 8.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = attachedFileUri?.let { getFileName(it, context) } ?: "Ningún archivo seleccionado",
                        onValueChange = {},
                        label = { Text("Archivo Adjunto") },
                        modifier = Modifier.weight(1f),
                        readOnly = true,
                        maxLines = 1,
                        supportingText = { Text("Opcional") }
                    )
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { filePickerLauncher.launch("*/*") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Seleccionar Archivo")
                }
                Spacer(Modifier.height(24.dp))
                Button(onClick = {
                    Toast.makeText(context, "Mensaje enviado. Gracias por contactarnos.", Toast.LENGTH_LONG).show()
                    navController.popBackStack()
                }, modifier = Modifier.fillMaxWidth(), enabled = isFormValid) {
                    Text("Enviar Consulta")
                }
            }
        }
    }
}

@Composable
private fun ContactInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, isClickable: Boolean = false, onClick: () -> Unit = {}) {
    val modifier = if (isClickable) Modifier.clickable(onClick = onClick) else Modifier
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.size(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactScreenPreview() {
    Proyecto_bmiTheme {
        ContactScreen(navController = rememberNavController())
    }
}