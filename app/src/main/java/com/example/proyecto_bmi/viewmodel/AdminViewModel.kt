package com.example.proyecto_bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.data.local.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val operationSuccess: String? = null,
    val operationError: String? = null
)

class AdminViewModel : ViewModel() {
    private val repository = PostRepository()
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState

    init {
        loadPosts()
    }

    fun loadPosts() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = repository.getPosts()
            _uiState.update { it.copy(posts = result, isLoading = false) }
        }
    }

    fun deletePost(id: Int) {
        viewModelScope.launch {
            val success = repository.deletePost(id)
            if (success) {
                _uiState.update { it.copy(operationSuccess = "Manual eliminado correctamente") }
                loadPosts()
            } else {
                _uiState.update { it.copy(operationError = "Error al eliminar manual") }
            }
        }
    }

    fun savePost(post: Post, isEdit: Boolean) {
        viewModelScope.launch {
            val success = if (isEdit) {
                repository.updatePost(post.id, post)
            } else {
                repository.createPost(post)
            }

            if (success) {
                _uiState.update { it.copy(operationSuccess = if(isEdit) "Actualizado" else "Creado") }
                loadPosts()
            } else {
                _uiState.update { it.copy(operationError = "Error al guardar") }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(operationSuccess = null, operationError = null) }
    }
}