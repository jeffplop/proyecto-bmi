package com.example.proyecto_bmi.ui.screens.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_bmi.viewmodel.UsuarioViewModel
import com.example.proyecto_bmi.ui.theme.Proyecto_bmiTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(navController: NavController, viewModel: UsuarioViewModel) {
    val estado by viewModel.estado.collectAsState()
    val registroExitoso by viewModel.registroExitoso.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val isFormValid = estado.errores.nombre == null && estado.errores.correo == null
            && estado.errores.clave == null && estado.errores.confirmClave == null
            && estado.errores.direccion == null && estado.aceptaTerminos
            && estado.nombre.isNotBlank() && estado.correo.isNotBlank()
            && estado.clave.isNotBlank() && estado.confirmClave.isNotBlank()
            && estado.direccion.isNotBlank()

    LaunchedEffect(Unit) { viewModel.resetAuthStatus() }
    LaunchedEffect(registroExitoso) { if (registroExitoso) navController.navigate(route = "resumen") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar a la pantalla anterior")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { if (isFormValid) viewModel.intentarRegistro() },
                        enabled = isFormValid
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Confirmar Registro",
                            tint = if (isFormValid) Color(0xFF4CAF50) else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, Color(0xFF0D47A1))
                    )
                )
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 24.dp, horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = 12.dp)
            ) {
                Icon(
                    Icons.Filled.AppRegistration,
                    contentDescription = "Registro de Usuario",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text("Crear Cuenta", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center), color = MaterialTheme.colorScheme.onSurface)
                Text("Ingrese sus datos para acceder al catálogo exclusivo.", style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center), color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = estado.nombre, onValueChange = viewModel::onNombreChange, label = { Text("Nombre de Usuario") },
                    isError = estado.errores.nombre != null,
                    supportingText = { estado.errores.nombre?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.correo, onValueChange = viewModel::onCorreoChange, label = { Text("Correo electrónico") },
                    isError = estado.errores.correo != null,
                    supportingText = { estado.errores.correo?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.direccion, onValueChange = viewModel::onDireccionChange, label = { Text("Dirección") },
                    isError = estado.errores.direccion != null,
                    supportingText = { estado.errores.direccion?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.clave,
                    onValueChange = viewModel::onClaveChange,
                    label = { Text("Contraseña") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = estado.errores.clave != null,
                    supportingText = { estado.errores.clave?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    }
                )

                OutlinedTextField(
                    value = estado.confirmClave,
                    onValueChange = viewModel::onConfirmClaveChange,
                    label = { Text("Confirmar Contraseña") },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = estado.errores.confirmClave != null,
                    supportingText = { estado.errores.confirmClave?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        val image = if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        val description = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = estado.aceptaTerminos, onCheckedChange = viewModel::onAceptarTerminosChange)
                    Spacer(Modifier.width(8.dp))
                    Text("Acepto los términos y condiciones")
                }

                val interactionSource = remember { MutableInteractionSource() }
                val isPressed = interactionSource.collectIsPressedAsState()
                val scale = animateFloatAsState(if (isPressed.value) 0.98f else 1f, label = "buttonScale")

                Button(
                    onClick = { viewModel.intentarRegistro() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { scaleX = scale.value; scaleY = scale.value },
                    interactionSource = interactionSource,
                    enabled = isFormValid
                ) {
                    Text("Registrarme")
                }

                Spacer(Modifier.height(8.dp))

                val interactionSourceLink = remember { MutableInteractionSource() }
                val isPressedLink by interactionSourceLink.collectIsPressedAsState()
                val pressedColorLink = if (isPressedLink) Color(0x10000000) else Color.Transparent

                val annotatedText = buildAnnotatedString {
                    append("¿Ya cuenta con un usuario? ")
                    pushStringAnnotation(tag = "LOGIN", annotation = "login")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) {
                        append("Iniciar Sesión")
                    }
                    pop()
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(pressedColorLink)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable(
                            interactionSource = interactionSourceLink,
                            indication = null,
                            onClick = {}
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    ClickableText(
                        text = annotatedText,
                        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                        onClick = { offset ->
                            annotatedText.getStringAnnotations(tag = "LOGIN", start = offset, end = offset).firstOrNull()?.let {
                                navController.navigate("login")
                            }
                        },
                    )
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

private val registroScreenPreviewViewModel: PreviewAuthViewModel by lazy {
    val mockDao = MockUsuarioDao()
    val mockRepository = MockUsuarioRepository(mockDao)
    PreviewAuthViewModel(mockRepository)
}

@Preview(showBackground = true)
@Composable
fun RegistroScreenPreview() {
    Proyecto_bmiTheme {
        RegistroScreen(navController = rememberNavController(), viewModel = registroScreenPreviewViewModel)
    }
}