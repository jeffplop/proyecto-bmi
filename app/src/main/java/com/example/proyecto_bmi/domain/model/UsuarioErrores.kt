package com.example.proyecto_bmi.domain.model

data class UsuarioErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val clave: String? = null,
    val confirmClave: String? = null,
    val direccion: String? = null
)
