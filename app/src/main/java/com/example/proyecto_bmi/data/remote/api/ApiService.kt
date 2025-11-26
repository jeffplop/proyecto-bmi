package com.example.proyecto_bmi.data.remote.api

import com.example.proyecto_bmi.data.remote.model.CategoryRemote
import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.data.remote.model.UserRemote
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("/posts")
    suspend fun getPosts(): List<Post>

    @GET("/posts/category/{id}")
    suspend fun getPostsByCategory(@Path("id") id: Int): List<Post>

    @GET("/categories")
    suspend fun getCategories(): List<CategoryRemote>

    @POST("/users/login")
    suspend fun login(@Body user: UserRemote): UserRemote?

    @POST("/users/register")
    suspend fun register(@Body user: UserRemote): UserRemote

    @PUT("/users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: UserRemote): UserRemote
}