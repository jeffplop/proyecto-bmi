package com.example.proyecto_bmi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto_bmi.navigation.AppScreens
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(
    navController: NavController,
    drawerState: DrawerState,
    scope: kotlinx.coroutines.CoroutineScope,
    userName: String = "Usuario",
    userRole: String = "USER",
    onLogout: () -> Unit
) {
    ModalDrawerSheet {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF2563EB), Color(0xFF1E40AF))
                    )
                )
                .padding(vertical = 48.dp, horizontal = 24.dp)
        ) {
            Column {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Hola, $userName",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if(userRole == "ADMIN") "Administrador" else "Miembro BMI",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxHeight()
        ) {
            DrawerItem(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                label = "Catálogo (Inicio)",
                onClick = {
                    navController.navigate(AppScreens.CatalogoScreen.route) {
                        popUpTo(AppScreens.CatalogoScreen.route) { inclusive = true }
                    }
                    scope.launch { drawerState.close() }
                }
            )

            DrawerItem(
                icon = Icons.Default.Wifi,
                label = "Manuales Online",
                onClick = {
                    navController.navigate(AppScreens.PostScreen.route)
                    scope.launch { drawerState.close() }
                }
            )

            DrawerItem(
                icon = Icons.Default.Favorite,
                label = "Favoritos",
                onClick = {
                    navController.navigate(AppScreens.FavoritosScreen.route)
                    scope.launch { drawerState.close() }
                }
            )

            DrawerItem(
                icon = Icons.Default.Person,
                label = "Mi Perfil",
                onClick = {
                    navController.navigate(AppScreens.PerfilScreen.route)
                    scope.launch { drawerState.close() }
                }
            )

            DrawerItem(
                icon = Icons.Default.SupportAgent,
                label = "Contacto",
                onClick = {
                    navController.navigate(AppScreens.ContactScreen.route)
                    scope.launch { drawerState.close() }
                }
            )

            if (userRole == "ADMIN") {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Administración", modifier = Modifier.padding(start = 16.dp), color = Color.Gray, fontSize = 12.sp)

                DrawerItem(
                    icon = Icons.Default.Settings,
                    label = "Gestionar Categorías",
                    iconColor = Color(0xFFF59E0B),
                    textColor = Color(0xFFF59E0B),
                    onClick = {
                        navController.navigate(AppScreens.AdminCategoryScreen.route)
                        scope.launch { drawerState.close() }
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            NavigationDrawerItem(
                label = { Text("Cerrar Sesión", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.AutoMirrored.Filled.Logout, null) },
                selected = false,
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color(0xFFFEF2F2),
                    unselectedTextColor = Color(0xFFEF4444),
                    unselectedIconColor = Color(0xFFEF4444)
                ),
                onClick = {
                    scope.launch { drawerState.close() }
                    onLogout()
                }
            )
        }
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    iconColor: Color = Color(0xFF1E293B),
    textColor: Color = Color(0xFF1E293B)
) {
    NavigationDrawerItem(
        label = { Text(label, fontWeight = FontWeight.Medium) },
        icon = { Icon(icon, null) },
        selected = false,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedIconColor = iconColor,
            unselectedTextColor = textColor
        ),
        onClick = onClick,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}