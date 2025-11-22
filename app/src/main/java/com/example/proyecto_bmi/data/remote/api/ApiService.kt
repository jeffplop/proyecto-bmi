package com.example.proyecto_bmi.data.remote.api

import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.data.remote.model.UserRemote
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET(value="/posts")
    suspend fun getPosts(): List<Post>

    @POST("/users/login")
    suspend fun login(@Body user: UserRemote): UserRemote?

    @POST("/users/register")
    suspend fun register(@Body user: UserRemote): UserRemote
}