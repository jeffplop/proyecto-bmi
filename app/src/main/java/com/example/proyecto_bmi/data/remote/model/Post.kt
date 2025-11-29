package com.example.proyecto_bmi.data.remote.model

data class Post(
    val userId: Int,
    val id: Int? = null,
    val title: String,
    val body: String,
    val pdfUrl: String? = null,
    val version: String? = "1.0",
    val fecha: String? = "--/--/----",
    val fabricante: String? = "Genérico",
    val isPremium: Boolean = false,
    val categoryId: Int? = null
)