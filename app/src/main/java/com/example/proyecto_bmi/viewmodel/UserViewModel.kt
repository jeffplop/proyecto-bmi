package com.example.proyecto_bmi.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.proyecto_bmi.model.AppScreen
import com.example.proyecto_bmi.model.UserData

class UserViewModel : ViewModel() {

    var currentScreen by mutableStateOf(AppScreen.RegistrationForm)
        private set

    var userData by mutableStateOf<UserData?>(null)
        private set

    fun registerUser(data: UserData) {
        userData = data
        currentScreen = AppScreen.Summary
    }

    fun navigateTo(screen: AppScreen) {
        currentScreen = screen
    }

    fun resetRegistration() {
        userData = null
        currentScreen = AppScreen.RegistrationForm
    }
}