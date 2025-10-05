package com.example.proyecto_bmi.model

data class UserData(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = ""
)

enum class AppScreen {
    RegistrationForm,
    Summary,
    Contact
}