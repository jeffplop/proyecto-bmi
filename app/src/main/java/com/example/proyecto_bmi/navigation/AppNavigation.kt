package com.example.proyecto_bmi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_bmi.data.local.AppDatabase
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import com.example.proyecto_bmi.ui.screens.auth.LoginScreen
import com.example.proyecto_bmi.ui.screens.auth.RegistroScreen
import com.example.proyecto_bmi.ui.screens.auth.ResumenScreen
import com.example.proyecto_bmi.ui.screens.misc.CatalogoScreen
import com.example.proyecto_bmi.ui.screens.misc.ContactScreen
import com.example.proyecto_bmi.ui.screens.misc.HomeScreen
import com.example.proyecto_bmi.viewmodel.UsuarioViewModel

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val navController = rememberNavController()

    val db = AppDatabase.getDatabase(context)
    val usuarioRepository = UsuarioRepository(db.usuarioDao())

    val factory = UsuarioViewModel.Factory(usuarioRepository)
    val usuarioViewModel: UsuarioViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable(route = "home") {
            HomeScreen(navController = navController)
        }
        composable(route = "login") {
            LoginScreen(navController, usuarioViewModel)
        }
        composable(route = "registro") {
            RegistroScreen(navController, usuarioViewModel)
        }
        composable(route = "resumen") {
            ResumenScreen(navController, usuarioViewModel)
        }
        composable(route = "contact") {
            ContactScreen(navController = navController)
        }
        composable(route = "catalogo") {
            CatalogoScreen(navController, usuarioViewModel)
        }
    }
}