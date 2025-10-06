package com.example.proyecto_bmi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fabricante")
data class FabricanteEntity(
    @PrimaryKey
    val id: Int,
    val nombre: String
)