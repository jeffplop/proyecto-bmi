package com.example.proyecto_bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_bmi.data.remote.model.CategoryRemote
import com.example.proyecto_bmi.data.repository.CatalogoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogoViewModel : ViewModel() {
    private val repository = CatalogoRepository()

    private val _categories = MutableStateFlow<List<CategoryRemote>>(emptyList())
    val categories: StateFlow<List<CategoryRemote>> = _categories.asStateFlow()

    private val _operationMessage = MutableStateFlow<String?>(null)
    val operationMessage: StateFlow<String?> = _operationMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchCategories()
    }

    fun fetchCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _categories.value = repository.getCategories()
            _isLoading.value = false
        }
    }

    fun saveCategory(category: CategoryRemote) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = if (category.id == 0) {
                repository.createCategory(category)
            } else {
                repository.updateCategory(category.id, category)
            }

            if (success) {
                _operationMessage.value = "Categoría guardada correctamente"
                fetchCategories()
            } else {
                _operationMessage.value = "Error al guardar categoría"
            }
            _isLoading.value = false
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.deleteCategory(id)
            if (success) {
                _operationMessage.value = "Categoría eliminada"
                fetchCategories()
            } else {
                _operationMessage.value = "Error al eliminar"
            }
            _isLoading.value = false
        }
    }

    fun clearMessage() {
        _operationMessage.value = null
    }
}