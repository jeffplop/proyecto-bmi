package com.example.proyecto_bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.data.local.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val repository: PostRepository = PostRepository()
) : ViewModel() {

    private val _postList = MutableStateFlow<List<Post>>(emptyList())
    val postList: StateFlow<List<Post>> = _postList

    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost: StateFlow<Post?> = _selectedPost

    private val _userRole = MutableStateFlow("USER")
    val userRole: StateFlow<String> = _userRole

    init {
        fetchPosts()
        fetchCurrentUserRole()
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

    fun fetchPostsByCategory(categoryId: Int) {
        viewModelScope.launch {
            try {
                val posts = repository.getPostsByCategory(categoryId)
                _postList.value = posts
            } catch (e: Exception) {
                _postList.value = emptyList()
            }
        }
    }

    fun getPostById(id: Int) {
        viewModelScope.launch {
            val post = _postList.value.find { it.id == id }
            _selectedPost.value = post
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