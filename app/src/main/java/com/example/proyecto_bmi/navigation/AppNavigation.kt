package com.example.proyecto_bmi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.example.proyecto_bmi.ui.screens.admin.AdminManualFormScreen
import com.example.proyecto_bmi.ui.screens.admin.AdminCategoryScreen
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
    val postViewModelFactory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return PostViewModel(usuarioRepository, sessionManager) as T
        }
    }

    val startDestination = if (sessionManager.getUserId() != -1) {
        AppScreens.CatalogoScreen.route
    } else {
        AppScreens.HomeScreen.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = AppScreens.HomeScreen.route) { HomeScreen(navController) }
        composable(route = AppScreens.LoginScreen.route) {
            val userViewModel: UsuarioViewModel = viewModel(factory = factory)
            LoginScreen(navController, userViewModel, sessionManager)
        }
        composable(route = AppScreens.RegistroScreen.route) {
            val userViewModel: UsuarioViewModel = viewModel(factory = factory)
            RegistroScreen(navController, userViewModel)
        }
        composable(route = AppScreens.ResumenScreen.route) {
            val userViewModel: UsuarioViewModel = viewModel(factory = factory)
            ResumenScreen(navController, userViewModel)
        }
        composable(route = AppScreens.ContactScreen.route) { ContactScreen(navController) }

        composable(route = AppScreens.CatalogoScreen.route) {
            val postViewModel: PostViewModel = viewModel(factory = postViewModelFactory)
            val catalogoViewModel: CatalogoViewModel = viewModel()
            val userViewModel: UsuarioViewModel = viewModel(factory = factory)
            CatalogoScreen(navController, userViewModel, catalogoViewModel, postViewModel)
        }

        composable(route = AppScreens.PostScreen.route) {
            val postViewModel: PostViewModel = viewModel(factory = postViewModelFactory)
            LaunchedEffect(Unit) { postViewModel.refreshAllData() }
            PostScreen(navController, postViewModel)
        }

        composable(route = AppScreens.AdminCategoryScreen.route) {
            val catalogoViewModel: CatalogoViewModel = viewModel()
            AdminCategoryScreen(navController, catalogoViewModel)
        }

        composable(route = AppScreens.FavoritosScreen.route) {
            val postViewModel: PostViewModel = viewModel(factory = postViewModelFactory)
            LaunchedEffect(Unit) { postViewModel.fetchFavorites() }
            FavoritosScreen(navController, postViewModel)
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

        composable(
            route = AppScreens.ManualScreen.route + "/{manualId}",
            arguments = listOf(navArgument("manualId") { type = NavType.StringType })
        ) { backStackEntry ->
            val manualId = backStackEntry.arguments?.getString("manualId")
            val postViewModel: PostViewModel = viewModel(factory = postViewModelFactory)
            ManualScreen(navController, manualId, postViewModel)
        }

        composable(
            route = AppScreens.CategoriaScreen.route + "/{categoriaId}",
            arguments = listOf(navArgument("categoriaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoriaId = backStackEntry.arguments?.getString("categoriaId")
            val postViewModel: PostViewModel = viewModel(factory = postViewModelFactory)
            CategoriaScreen(navController, postViewModel, categoriaId)
        }

        composable(
            route = "admin_form?manualId={manualId}",
            arguments = listOf(navArgument("manualId") {
                nullable = true
                defaultValue = null
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val manualIdStr = backStackEntry.arguments?.getString("manualId")
            val manualId = manualIdStr?.toIntOrNull()
            val postViewModel: PostViewModel = viewModel(factory = postViewModelFactory)
            AdminManualFormScreen(navController, postViewModel, manualId)
        }
    }
}