package com.example.proyecto_bmi.ui.screens.misc

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.proyecto_bmi.data.local.SessionManager
import com.example.proyecto_bmi.navigation.AppScreens
import com.example.proyecto_bmi.ui.components.AppDrawer
import com.example.proyecto_bmi.viewmodel.PerfilViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Context.createImageUri(): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir = File(cacheDir, "my_images")
    if (!storageDir.exists()) storageDir.mkdirs()
    val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
    return FileProvider.getUriForFile(this, "${packageName}.provider", imageFile)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController, viewModel: PerfilViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val sessionManager = SessionManager(context)

    LaunchedEffect(state.updateSuccess) {
        if (state.updateSuccess) {
            Toast.makeText(context, "¡Perfil guardado y sincronizado!", Toast.LENGTH_SHORT).show()
            viewModel.resetSuccess()
        }
    }

    LaunchedEffect(state.fotoUri) {
        if (state.fotoUri != null) {
            try { imageUri = Uri.parse(state.fotoUri) } catch (e: Exception) {}
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) { imageUri = uri; viewModel.onFotoChange(uri.toString()) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success && tempCameraUri != null) { imageUri = tempCameraUri; viewModel.onFotoChange(tempCameraUri.toString()) }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            val newUri = context.createImageUri()
            tempCameraUri = newUri
            cameraLauncher.launch(newUri)
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Actualizar Fotografía") },
            confirmButton = {
                Button(onClick = { showDialog = false; cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))) { Text("Cámara") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false; imagePickerLauncher.launch("image/*") }) { Text("Galería") }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                navController = navController,
                drawerState = drawerState,
                scope = scope,
                userName = state.nombre,
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate(AppScreens.HomeScreen.route) {
                        popUpTo(0)
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mi Perfil", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2563EB))
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8FAFC))
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(brush = Brush.verticalGradient(colors = listOf(Color(0xFF2563EB), Color(0xFF1E40AF))))
                ) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.align(Alignment.BottomCenter).offset(y = 60.dp).clickable { showDialog = true }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUri ?: "https://via.placeholder.com/150"),
                            contentDescription = "Foto",
                            modifier = Modifier.size(140.dp).clip(CircleShape).background(Color.White).padding(4.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF059669)).padding(8.dp), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
                Spacer(Modifier.height(80.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Información Personal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Spacer(Modifier.height(20.dp))

                        OutlinedTextField(
                            value = state.nombre, onValueChange = { viewModel.onNombreChange(it) },
                            label = { Text("Nombre Completo") }, leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFF2563EB)) },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2563EB))
                        )
                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = state.correo, onValueChange = { viewModel.onCorreoChange(it) },
                            label = { Text("Correo Electrónico") }, leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF2563EB)) },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2563EB))
                        )
                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = state.telefono, onValueChange = { viewModel.onTelefonoChange(it) },
                            label = { Text("Número Telefónico") }, leadingIcon = { Icon(Icons.Default.Phone, null, tint = Color(0xFF2563EB)) },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2563EB))
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.guardarCambios() },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(50.dp),
                    enabled = !state.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    if (state.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text("Guardar Cambios", fontSize = 16.sp)
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}