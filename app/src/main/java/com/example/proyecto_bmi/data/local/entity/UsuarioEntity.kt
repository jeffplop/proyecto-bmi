package com.example.proyecto_bmi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val clave: String,
    val direccion: String,
    val tipoUsuario: String = "Estandar",
    val fechaIngreso: Long = System.currentTimeMillis()
)