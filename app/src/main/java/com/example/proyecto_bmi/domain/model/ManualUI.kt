package com.example.proyecto_bmi.domain.model

data class ManualUI(
    val id: String,
    val title: String,
    val subtitle: String,
    val isPremium: Boolean = false
)