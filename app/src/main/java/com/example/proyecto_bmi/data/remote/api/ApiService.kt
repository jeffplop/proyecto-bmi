package com.example.proyecto_bmi.data.remote.api

import com.example.proyecto_bmi.data.remote.model.Post
import retrofit2.http.GET

interface ApiService {
    @GET(value="/posts")
    suspend fun getPosts(): List<Post>
}