package com.example.proyecto_bmi.data.local.repository

import com.example.proyecto_bmi.data.remote.api.RetrofitInstance
import com.example.proyecto_bmi.data.remote.model.Favorite
import com.example.proyecto_bmi.data.remote.model.Post

class PostRepository {
    suspend fun getPosts(): List<Post> {
        return try {
            RetrofitInstance.api.getPosts()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPostById(id: Int): Post? {
        return try {
            RetrofitInstance.api.getPostById(id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getPostsByCategory(categoryId: Int): List<Post> {
        return try {
            RetrofitInstance.api.getPostsByCategory(categoryId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createPost(post: Post): Boolean {
        return try {
            RetrofitInstance.api.createPost(post)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updatePost(id: Int, post: Post): Boolean {
        return try {
            RetrofitInstance.api.updatePost(id, post)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deletePost(id: Int): Boolean {
        return try {
            RetrofitInstance.api.deletePost(id)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getFavorites(userId: Int): List<Favorite> {
        return try {
            RetrofitInstance.api.getUserFavorites(userId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun toggleFavorite(userId: Int, postId: Int, isCurrentlyFavorite: Boolean): Boolean {
        return try {
            if (isCurrentlyFavorite) {
                RetrofitInstance.api.removeFavorite(userId, postId)
            } else {
                RetrofitInstance.api.addFavorite(Favorite(userId = userId, postId = postId))
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}