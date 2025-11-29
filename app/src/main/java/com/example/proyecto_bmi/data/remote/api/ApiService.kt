package com.example.proyecto_bmi.data.remote.api

import com.example.proyecto_bmi.data.remote.model.CategoryRemote
import com.example.proyecto_bmi.data.remote.model.Favorite
import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.data.remote.model.UserRemote
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {
    @GET("/posts")
    suspend fun getPosts(): List<Post>

    @GET("/posts/{id}")
    suspend fun getPostById(@Path("id") id: Int): Post

    @GET("/posts/category/{id}")
    suspend fun getPostsByCategory(@Path("id") id: Int): List<Post>

    @POST("/posts")
    suspend fun createPost(@Body post: Post): Post

    @PUT("/posts/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body post: Post): Post

    @DELETE("/posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): Boolean

    @GET("/categories")
    suspend fun getCategories(): List<CategoryRemote>

    @GET("/favorites/user/{userId}")
    suspend fun getUserFavorites(@Path("userId") userId: Int): List<Favorite>

    @POST("/favorites/add")
    suspend fun addFavorite(@Body favorite: Favorite): Favorite

    @DELETE("/favorites/remove/{userId}/{postId}")
    suspend fun removeFavorite(@Path("userId") userId: Int, @Path("postId") postId: Int): ResponseBody

    @POST("/users/login")
    suspend fun login(@Body user: UserRemote): UserRemote?

    @POST("/users/register")
    suspend fun register(@Body user: UserRemote): UserRemote

    @PUT("/users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: UserRemote): ResponseBody
}