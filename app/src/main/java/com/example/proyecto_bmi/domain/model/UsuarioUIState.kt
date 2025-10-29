package com.example.proyecto_bmi.domain.model

data class UsuarioUiState(
    val nombre: String = "",
    val nombreError: String? = null,

    val correo: String = "",
    val correoError: String? = null,

    val clave: String = "",
    val claveError: String? = null,

    val confirmClave: String = "",
    val confirmClaveError: String? = null,

    val direccion: String = "",
    val direccionError: String? = null,

    val aceptaTerminos: Boolean = false,

    val loginError: String? = null,
    val loginLoading: Boolean = false
)