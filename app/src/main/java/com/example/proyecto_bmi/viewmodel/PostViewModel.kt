package com.example.proyecto_bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_bmi.data.local.SessionManager
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val sessionManager: SessionManager,
    private val repository: PostRepository = PostRepository()
) : ViewModel() {

    private val _postList = MutableStateFlow<List<Post>>(emptyList())
    val postList: StateFlow<List<Post>> = _postList

    private val _favoritesIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoritesIds: StateFlow<Set<Int>> = _favoritesIds

    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost: StateFlow<Post?> = _selectedPost

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _userRole = MutableStateFlow("USER")
    val userRole: StateFlow<String> = _userRole

    private val _favoriteMessage = MutableStateFlow<String?>(null)
    val favoriteMessage: StateFlow<String?> = _favoriteMessage

    init {
        fetchPosts()
        fetchCurrentUserRole()
        fetchFavorites()
    }

    fun fetchPosts() {
        viewModelScope.launch {
            try {
                val posts = repository.getPosts()
                _postList.value = posts
            } catch (e: Exception) {
                _postList.value = emptyList()
            }
        }
    }

    fun fetchFavorites() {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            viewModelScope.launch {
                try {
                    val favs = repository.getFavorites(userId)
                    _favoritesIds.value = favs.map { it.postId }.toSet()
                } catch (e: Exception) { }
            }
        }
    }

    fun toggleFavorite(post: Post) {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        val isFav = _favoritesIds.value.contains(post.id)

        viewModelScope.launch {
            val success = repository.toggleFavorite(userId, post.id, isFav)
            if (success) {
                if (isFav) {
                    _favoritesIds.update { it - post.id }
                    _favoriteMessage.value = "Eliminado de Favoritos"
                } else {
                    _favoritesIds.update { it + post.id }
                    _favoriteMessage.value = "Agregado a Favoritos"
                }
            }
        }
    }

    fun clearMessage() { _favoriteMessage.value = null }

    fun fetchPostsByCategory(categoryId: Int) {
        viewModelScope.launch {
            try {
                _postList.value = repository.getPostsByCategory(categoryId)
            } catch (e: Exception) {
                _postList.value = emptyList()
            }
        }
    }

    fun getPostById(id: Int) {
        _isLoading.value = true
        _errorMessage.value = null
        _selectedPost.value = null

        viewModelScope.launch {
            val post = repository.getPostById(id)
            if (post != null) {
                _selectedPost.value = post
            } else {
                _errorMessage.value = "Error al cargar manual."
            }
            _isLoading.value = false
        }
    }

    private fun fetchCurrentUserRole() {
        viewModelScope.launch {
            val usuarios = usuarioRepository.obtenerTodosLosUsuariosLocalmente()
            if (usuarios.isNotEmpty()) {
                val usuarioActual = usuarios.last()
                _userRole.value = if (usuarioActual.tipoUsuario == "Premium") "PREMIUM" else "USER"
            }
        }
    }
}