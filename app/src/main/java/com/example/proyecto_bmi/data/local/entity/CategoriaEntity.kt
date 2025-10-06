package com.example.proyecto_bmi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categoria")
data class CategoriaEntity(
    @PrimaryKey
    val id: Int,
    val nombre: String
)