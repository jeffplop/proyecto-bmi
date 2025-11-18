package com.example.proyecto_bmi.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppScreens(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object HomeScreen : AppScreens("home", "Inicio", Icons.Default.Home)
    object LoginScreen : AppScreens("login", "Login", Icons.Default.Home)
    object RegistroScreen : AppScreens("registro", "Registro", Icons.Default.Home)
    object ResumenScreen : AppScreens("resumen", "Resumen", Icons.Default.Home)
    object CatalogoScreen : AppScreens("catalogo", "Catálogo", Icons.AutoMirrored.Filled.MenuBook)
    object ContactScreen : AppScreens("contact", "Contacto y Soporte", Icons.AutoMirrored.Filled.ContactSupport)
    object FavoritosScreen : AppScreens("favoritos", "Mis Favoritos", Icons.Default.Favorite)
    object PerfilScreen : AppScreens("perfil", "Mi Perfil", Icons.Default.Person)
    object ManualScreen : AppScreens("manual", "Manual", Icons.AutoMirrored.Filled.MenuBook)
    object CategoriaScreen : AppScreens("categoria", "Categoría", Icons.AutoMirrored.Filled.MenuBook)
    object PostScreen : AppScreens("posts", "Posts (API)", Icons.Default.Api)
}