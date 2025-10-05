package com.example.proyecto_bmi.domain.model

data class UsuarioUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = "",
    val aceptaTerminos: Boolean = false,
    val errores: UsuarioErrores = UsuarioErrores()
)