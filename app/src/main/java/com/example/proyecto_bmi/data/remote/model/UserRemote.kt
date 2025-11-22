package com.example.proyecto_bmi.data.remote.model

data class UserRemote(
    val id: Int? = null,
    val nombre: String,
    val email: String,
    val password: String,
    val direccion: String,
    val role: String? = null
)