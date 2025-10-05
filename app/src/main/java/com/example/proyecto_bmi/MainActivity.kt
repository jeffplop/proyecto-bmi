package com.example.proyecto_bmi
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.proyecto_bmi.navigation.AppNavigation
import com.example.proyecto_bmi.ui.theme.Proyecto_bmiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Proyecto_bmiTheme {
                Scaffold { innerPadding ->
                    Box(modifier = Modifier.padding(paddingValues = innerPadding)) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}