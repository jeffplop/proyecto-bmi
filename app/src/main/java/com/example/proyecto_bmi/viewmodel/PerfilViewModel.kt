package com.example.proyecto_bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_bmi.data.local.SessionManager
import com.example.proyecto_bmi.data.local.entity.UsuarioEntity
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PerfilUiState(
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val fotoUri: String? = null,
    val isLoading: Boolean = false,
    val updateSuccess: Boolean = false,
    val userEntity: UsuarioEntity? = null
)

class PerfilViewModel(
    private val repository: UsuarioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState

    init {
        cargarUsuarioActual()
    }

    fun cargarUsuarioActual() {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            viewModelScope.launch {
                val usuario = repository.obtenerUsuarioPorId(userId)
                if (usuario != null) {
                    _uiState.update {
                        it.copy(
                            nombre = usuario.nombre,
                            correo = usuario.email,
                            telefono = usuario.telefono,
                            fotoUri = usuario.fotoUri,
                            userEntity = usuario
                        )
                    }
                }
            }
        }
    }

    fun onNombreChange(nuevo: String) { _uiState.update { it.copy(nombre = nuevo) } }

    fun onCorreoChange(nuevo: String) { _uiState.update { it.copy(correo = nuevo) } }

    fun onTelefonoChange(nuevo: String) { _uiState.update { it.copy(telefono = nuevo) } }
    fun onFotoChange(nuevaUri: String?) { _uiState.update { it.copy(fotoUri = nuevaUri) } }

    fun guardarCambios() {
        val currentUser = _uiState.value.userEntity ?: return

        _uiState.update { it.copy(isLoading = true, updateSuccess = false) }

        val usuarioActualizado = currentUser.copy(
            nombre = _uiState.value.nombre,
            email = _uiState.value.correo,
            telefono = _uiState.value.telefono,
            fotoUri = _uiState.value.fotoUri
        )

        viewModelScope.launch {
            repository.actualizarPerfil(usuarioActualizado)
            _uiState.update { it.copy(isLoading = false, updateSuccess = true) }
            cargarUsuarioActual()
        }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(updateSuccess = false) }
    }
}