package com.example.proyecto_bmi.data.local.repository

import com.example.proyecto_bmi.data.remote.api.RetrofitInstance
import com.example.proyecto_bmi.data.remote.model.Favorite
import com.example.proyecto_bmi.data.remote.model.Post

class PostRepository {

    suspend fun getPosts(): List<Post> {
        return try {
            RetrofitInstance.api.getPosts()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getPostById(id: Int): Post? {
        return try {
            RetrofitInstance.api.getPostById(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getPostsByCategory(categoryId: Int): List<Post> {
        return try {
            RetrofitInstance.api.getPostsByCategory(categoryId)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getFavorites(userId: Int): List<Favorite> {
        return try {
            RetrofitInstance.api.getUserFavorites(userId)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun toggleFavorite(userId: Int, postId: Int, isFavorite: Boolean): Boolean {
        return try {
            if (isFavorite) {
                RetrofitInstance.api.removeFavorite(userId, postId)
            } else {
                RetrofitInstance.api.addFavorite(Favorite(id = 0, userId = userId, postId = postId))
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}