package com.example.proyecto_bmi.data.repository

import com.example.proyecto_bmi.data.remote.api.RetrofitInstance
import com.example.proyecto_bmi.data.remote.model.CategoryRemote

class CatalogoRepository {
    suspend fun getCategories(): List<CategoryRemote> {
        return try {
            RetrofitInstance.api.getCategories()
        } catch (e: Exception) {
            emptyList()
        }
    }
}