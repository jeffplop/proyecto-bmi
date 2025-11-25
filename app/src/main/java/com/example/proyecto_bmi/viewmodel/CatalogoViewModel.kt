package com.example.proyecto_bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_bmi.data.remote.model.CategoryRemote
import com.example.proyecto_bmi.data.repository.CatalogoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CatalogoViewModel : ViewModel() {
    private val repository = CatalogoRepository()

    private val _categories = MutableStateFlow<List<CategoryRemote>>(emptyList())
    val categories: StateFlow<List<CategoryRemote>> = _categories

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _categories.value = repository.getCategories()
        }
    }
}