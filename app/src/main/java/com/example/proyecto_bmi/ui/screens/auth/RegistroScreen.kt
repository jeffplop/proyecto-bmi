package com.example.proyecto_bmi.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto_bmi.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.estado.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val isFormValid = estado.nombre.isNotBlank() && estado.correo.isNotBlank() &&
            estado.clave.isNotBlank() && estado.confirmClave.isNotBlank() &&
            estado.telefono.isNotBlank() && estado.aceptaTerminos

    LaunchedEffect(Unit) { viewModel.resetAuthStatus() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2563EB))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.primary, Color(0xFF0D47A1))))
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.9f).padding(vertical = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Filled.AppRegistration, null, tint = Color(0xFF2563EB), modifier = Modifier.size(64.dp))
                    Text("Crear Cuenta", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1E293B))

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = estado.nombre, onValueChange = viewModel::onNombreChange, label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = estado.correo, onValueChange = viewModel::onCorreoChange, label = { Text("Correo") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = estado.telefono, onValueChange = viewModel::onTelefonoChange, label = { Text("Teléfono (+56...)") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    OutlinedTextField(
                        value = estado.clave, onValueChange = viewModel::onClaveChange, label = { Text("Contraseña") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null) } }
                    )
                    OutlinedTextField(
                        value = estado.confirmClave, onValueChange = viewModel::onConfirmClaveChange, label = { Text("Confirmar Contraseña") },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        trailingIcon = { IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Icon(if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null) } }
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = estado.aceptaTerminos, onCheckedChange = viewModel::onAceptarTerminosChange)
                        Text("Acepto los términos", style = MaterialTheme.typography.bodyMedium)
                    }

                    Button(
                        onClick = { viewModel.intentarRegistro(); navController.navigate("resumen") },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = isFormValid,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Text("Registrarme", fontSize = 16.sp)
                    }

                    Spacer(Modifier.height(8.dp))

                    val annotatedText = buildAnnotatedString {
                        append("¿Ya tienes cuenta? ")
                        pushStringAnnotation(tag = "LOGIN", annotation = "login")
                        withStyle(style = SpanStyle(color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) { append("Iniciar Sesión") }
                        pop()
                    }

                    ClickableText(
                        text = annotatedText,
                        onClick = { offset -> annotatedText.getStringAnnotations(tag = "LOGIN", start = offset, end = offset).firstOrNull()?.let { navController.navigate("login") } }
                    )
                }
            }
        }
    }
}