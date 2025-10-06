package com.example.proyecto_bmi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "manual",
    foreignKeys = [
        ForeignKey(
            entity = FabricanteEntity::class,
            parentColumns = ["id"],
            childColumns = ["fabricanteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["fabricanteId"]),
        Index(value = ["categoriaId"])
    ]
)
data class ManualEntity(
    @PrimaryKey
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val urlPdf: String,
    val esBasico: Boolean,
    val fechaPublicacion: Long,
    val fabricanteId: Int,
    val categoriaId: Int
)