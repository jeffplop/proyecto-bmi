package com.example.proyecto_bmi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_bmi.data.local.AppDatabase
import com.example.proyecto_bmi.data.local.SessionManager
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import com.example.proyecto_bmi.ui.screens.auth.LoginScreen
import com.example.proyecto_bmi.ui.screens.auth.RegistroScreen
import com.example.proyecto_bmi.ui.screens.auth.ResumenScreen
import com.example.proyecto_bmi.ui.screens.categorias.CategoriaScreen
import com.example.proyecto_bmi.ui.screens.manuales.ManualScreen
import com.example.proyecto_bmi.ui.screens.misc.*
import com.example.proyecto_bmi.ui.screens.remote.PostScreen
import com.example.proyecto_bmi.viewmodel.CatalogoViewModel
import com.example.proyecto_bmi.viewmodel.PerfilViewModel
import com.example.proyecto_bmi.viewmodel.PostViewModel
import com.example.proyecto_bmi.viewmodel.UsuarioViewModel

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val navController = rememberNavController()

    val db = AppDatabase.getDatabase(context)
    val usuarioRepository = UsuarioRepository(db.usuarioDao())
    val sessionManager = SessionManager(context)

    val factory = UsuarioViewModel.Factory(usuarioRepository)
    val usuarioViewModel: UsuarioViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = AppScreens.HomeScreen.route
    ) {
        composable(route = AppScreens.HomeScreen.route) {
            HomeScreen(navController = navController)
        }

        composable(route = AppScreens.LoginScreen.route) {
            LoginScreen(navController, usuarioViewModel, sessionManager)
        }

        composable(route = AppScreens.RegistroScreen.route) {
            RegistroScreen(navController, usuarioViewModel)
        }

        composable(route = AppScreens.ResumenScreen.route) {
            ResumenScreen(navController, usuarioViewModel)
        }

        composable(route = AppScreens.ContactScreen.route) {
            ContactScreen(navController = navController)
        }

        composable(route = AppScreens.CatalogoScreen.route) {
            val postViewModel = androidx.lifecycle.viewmodel.compose.viewModel<PostViewModel>(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return PostViewModel(usuarioRepository) as T
                    }
                }
            )
            val catalogoViewModel: CatalogoViewModel = viewModel()

            CatalogoScreen(navController, usuarioViewModel, catalogoViewModel, postViewModel)
        }

        composable(route = AppScreens.FavoritosScreen.route) {
            FavoritosScreen(navController)
        }

        composable(route = AppScreens.PerfilScreen.route) {
            val perfilViewModel = androidx.lifecycle.viewmodel.compose.viewModel<PerfilViewModel>(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return PerfilViewModel(usuarioRepository, sessionManager) as T
                    }
                }
            )
            PerfilScreen(navController, perfilViewModel)
        }

        composable(route = AppScreens.ManualScreen.route + "/{manualId}") { backStackEntry ->
            ManualScreen(navController, backStackEntry.arguments?.getString("manualId"))
        }

        composable(route = AppScreens.CategoriaScreen.route + "/{categoriaId}") { backStackEntry ->
            val categoriaId = backStackEntry.arguments?.getString("categoriaId")
            val postViewModel = androidx.lifecycle.viewmodel.compose.viewModel<PostViewModel>(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return PostViewModel(usuarioRepository) as T
                    }
                }
            )
            CategoriaScreen(navController, postViewModel, categoriaId)
        }

        composable(route = AppScreens.PostScreen.route) {
            val postViewModel = androidx.lifecycle.viewmodel.compose.viewModel<PostViewModel>(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return PostViewModel(usuarioRepository) as T
                    }
                }
            )
            LaunchedEffect(Unit) {
                postViewModel.fetchPosts()
            }
            PostScreen(navController, postViewModel)
        }
    }
}