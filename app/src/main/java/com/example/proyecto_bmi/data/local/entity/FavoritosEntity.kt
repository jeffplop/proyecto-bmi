package com.example.proyecto_bmi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "favoritos",
    primaryKeys = ["usuarioId", "manualId"],
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ManualEntity::class,
            parentColumns = ["id"],
            childColumns = ["manualId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FavoritosEntity(
    val usuarioId: Int,
    val manualId: Int,
    val fechaAgregado: Long = System.currentTimeMillis()
)