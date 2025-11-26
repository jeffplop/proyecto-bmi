package com.example.proyecto_bmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.proyecto_bmi.navigation.AppNavigation
import com.example.proyecto_bmi.ui.theme.Proyecto_bmiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            Proyecto_bmiTheme {
                AppNavigation()
            }
        }
    }
}