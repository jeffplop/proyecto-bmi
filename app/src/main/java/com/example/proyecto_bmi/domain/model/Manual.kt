package com.example.proyecto_bmi.domain.model

data class Manual(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val urlPdf: String,
    val esBasico: Boolean,
    val fechaPublicacion: String,
    val fabricanteId: Int,
    val categoriaId: Int
)