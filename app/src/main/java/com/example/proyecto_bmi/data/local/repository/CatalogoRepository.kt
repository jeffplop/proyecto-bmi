package com.example.proyecto_bmi.data.local.repository

import com.example.proyecto_bmi.data.remote.api.RetrofitInstance
import com.example.proyecto_bmi.data.remote.model.CategoryRemote

class CatalogoRepository {
    suspend fun getCategories(): List<CategoryRemote> {
        return try {
            val response = RetrofitInstance.api.getCategories()
            response
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun createCategory(category: CategoryRemote): Boolean {
        return try {
            RetrofitInstance.api.createCategory(category)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateCategory(id: Int, category: CategoryRemote): Boolean {
        return try {
            RetrofitInstance.api.updateCategory(id, category)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteCategory(id: Int): Boolean {
        return try {
            RetrofitInstance.api.deleteCategory(id)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}