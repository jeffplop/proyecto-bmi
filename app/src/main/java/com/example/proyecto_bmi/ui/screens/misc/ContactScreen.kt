package com.example.proyecto_bmi.ui.screens.misc

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_bmi.R
import com.example.proyecto_bmi.ui.theme.Proyecto_bmiTheme

@Composable
fun ContactScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var locationInfo by remember { mutableStateOf("Ubicación: Desconocida") }
    val context = LocalContext.current
    val notificationChannelId = "contacto_channel"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                getLocation(context) { newLocation ->
                    locationInfo = "Lat: ${newLocation.latitude}, Lon: ${newLocation.longitude}"
                }
            } else {
                locationInfo = "Ubicación: Permiso denegado"
            }
        }
    )

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    fun sendNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(notificationChannelId, "Notificaciones de Contacto", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }

        val builder = NotificationCompat.Builder(context, notificationChannelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("B.M.I. Contacto")
            .setContentText("Tu mensaje ha sido enviado correctamente. Responderemos pronto.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(1, builder.build())
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Contacto", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Tu Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Tu Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = mensaje,
            onValueChange = { mensaje = it },
            label = { Text("Describe tu problema") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
        )
        Spacer(Modifier.height(16.dp))

        Text(locationInfo, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                sendNotification()
                navController.navigate("home")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = nombre.isNotBlank() && correo.isNotBlank() && mensaje.isNotBlank()
        ) {
            Text("Enviar Mensaje")
        }
        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController.navigate("home") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a Inicio")
        }
    }
}

fun getLocation(context: Context, onLocationReceived: (Location) -> Unit) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        lastLocation?.let(onLocationReceived)
    }
}

@Preview(showBackground = true)
@Composable
fun ContactScreenPreview() {
    Proyecto_bmiTheme {
        ContactScreen(navController = rememberNavController())
    }
}