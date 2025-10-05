package com.example.proyecto_bmi.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_bmi.ui.screens.auth.RegistroScreen
import com.example.proyecto_bmi.ui.screens.auth.ResumenScreen
import com.example.proyecto_bmi.ui.screens.misc.ContactScreen
import com.example.proyecto_bmi.ui.screens.misc.HomeScreen
import com.example.proyecto_bmi.viewmodel.UsuarioViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val usuarioViewModel: UsuarioViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable(route = "home") {
            HomeScreen(navController = navController)
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
    }
}