package com.example.proyecto_bmi.data.local.repository

import com.example.proyecto_bmi.data.remote.api.RetrofitInstance
import com.example.proyecto_bmi.data.remote.model.Post

class PostRepository {
    suspend fun getPosts(): List<Post> {
        return RetrofitInstance.api.getPosts()
    }
}