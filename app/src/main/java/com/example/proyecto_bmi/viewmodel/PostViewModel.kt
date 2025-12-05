package com.example.proyecto_bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_bmi.data.local.SessionManager
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.data.local.repository.PostRepository
import com.example.proyecto_bmi.data.remote.model.CategoryRemote
import com.example.proyecto_bmi.data.local.repository.CatalogoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val sessionManager: SessionManager,
    private val repository: PostRepository = PostRepository(),
    private val catalogoRepository: CatalogoRepository = CatalogoRepository()
) : ViewModel() {

    private val _postList = MutableStateFlow<List<Post>>(emptyList())
    val postList: StateFlow<List<Post>> = _postList

    private val _categories = MutableStateFlow<List<CategoryRemote>>(emptyList())
    val categories: StateFlow<List<CategoryRemote>> = _categories

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

    private val _operationMessage = MutableStateFlow<String?>(null)
    val operationMessage: StateFlow<String?> = _operationMessage

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    init {
        refreshAllData()
    }

    fun refreshAllData() {
        fetchCurrentUserRole()
        fetchPosts()
        fetchFavorites()
        fetchCategories()
    }

    fun fetchCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val cats = catalogoRepository.getCategories()
            _categories.value = cats
        }
    }

    private fun fetchCurrentUserRole() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = sessionManager.getUserId()
            if (userId != -1) {
                val usuario = usuarioRepository.obtenerUsuarioPorId(userId)
                val role = when {
                    usuario?.email?.lowercase()?.contains("admin") == true -> "ADMIN"
                    usuario?.tipoUsuario.equals("Premium", ignoreCase = true) -> "PREMIUM"
                    usuario?.tipoUsuario.equals("ADMIN", ignoreCase = true) -> "ADMIN"
                    else -> "USER"
                }
                _userRole.value = role
            }
        }
    }

    fun fetchPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val posts = repository.getPosts()
            _postList.value = posts
            _isLoading.value = false
        }
    }

    fun fetchFavorites() {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            viewModelScope.launch(Dispatchers.IO) {
                val favs = repository.getFavorites(userId)
                _favoritesIds.value = favs.map { it.postId }.toSet()
            }
        }
    }

    fun toggleFavorite(post: Post) {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        val isFav = _favoritesIds.value.contains(post.id ?: 0)
        val postId = post.id ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.toggleFavorite(userId, postId, isFav)
            if (success) {
                if (isFav) {
                    _favoritesIds.update { it - postId }
                    _operationMessage.value = "Eliminado de Favoritos"
                } else {
                    _favoritesIds.update { it + postId }
                    _operationMessage.value = "Agregado a Favoritos"
                }
            }
        }
    }

    fun getPostById(id: Int) {
        _isLoading.value = true
        _selectedPost.value = null
        _errorMessage.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val post = repository.getPostById(id)
            if (post != null) {
                _selectedPost.value = post
            } else {
                _errorMessage.value = "Error al cargar el manual."
            }
            _isLoading.value = false
        }
    }

    fun deletePost(id: Int?) {
        if (id == null) return
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val success = repository.deletePost(id)
            if (success) {
                _operationMessage.value = "Manual eliminado"
                fetchPosts()
            } else {
                _errorMessage.value = "Error al eliminar"
            }
            _isLoading.value = false
        }
    }

    fun saveOrUpdatePost(post: Post, isEdit: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val userId = sessionManager.getUserId()

            val postToSave = post.copy(userId = if (userId != -1) userId else 1)

            val success = if (isEdit && post.id != null) {
                repository.updatePost(post.id, postToSave)
            } else {
                repository.createPost(postToSave)
            }

            withContext(Dispatchers.Main) {
                if (success) {
                    _operationMessage.value = if (isEdit) "Manual actualizado" else "Manual creado"
                    fetchPosts()
                    delay(200)
                    _saveSuccess.value = true
                } else {
                    _errorMessage.value = "Error de conexión. Intente nuevamente."
                }
                _isLoading.value = false
            }
        }
    }

    fun resetSaveStatus() {
        _saveSuccess.value = false
        _selectedPost.value = null
    }

    fun fetchPostsByCategory(categoryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val posts = repository.getPostsByCategory(categoryId)
            _postList.value = posts
            _isLoading.value = false
        }
    }

    fun clearMessage() { _operationMessage.value = null }
}