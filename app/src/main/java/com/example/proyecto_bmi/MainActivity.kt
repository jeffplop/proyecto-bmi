package com.example.proyecto_bmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto_bmi.ui.theme.RegistroScreen
import com.example.proyecto_bmi.ui.theme.ResumenScreen
import com.example.proyecto_bmi.ui.theme.Proyecto_bmiTheme
import com.example.proyecto_bmi.viewmodel.UsuarioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Proyecto_bmiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    viewModel: UsuarioViewModel = viewModel()
) {
    val estadoUi by viewModel.uiState.collectAsState()

    if (!estadoUi.isRegistered) {
        RegistroScreen(
            estadoUi = estadoUi,
            onNombreChange = viewModel::onNombreChange,
            onCorreoChange = viewModel::onCorreoChange,
            onClaveChange = viewModel::onClaveChange,
            onDireccionChange = viewModel::onDireccionChange,
            onRegisterClick = viewModel::validarYRegistrar,
            modifier = modifier
        )
    } else {
        ResumenScreen(
            estadoUi = estadoUi,
            onBackClick = viewModel::volverAFormulario,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    Proyecto_bmiTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            AppNavigation(modifier = Modifier.fillMaxSize())
        }
    }
}