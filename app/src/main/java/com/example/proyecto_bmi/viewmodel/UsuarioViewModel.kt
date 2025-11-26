package com.example.proyecto_bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto_bmi.data.local.entity.UsuarioEntity
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import com.example.proyecto_bmi.domain.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class UsuarioViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(UsuarioUiState())

    open val estado: StateFlow<UsuarioUiState> = _estado

    private val _loginExitoso = MutableStateFlow(false)
    val loginExitoso: StateFlow<Boolean> = _loginExitoso

    private val _usuarioLogueado = MutableStateFlow<UsuarioEntity?>(null)
    val usuarioLogueado: StateFlow<UsuarioEntity?> = _usuarioLogueado

    fun onCorreoChange(valor: String) { _estado.update { it.copy(correo = valor, correoError = null, loginError = null) } }
    fun onClaveChange(valor: String) { _estado.update { it.copy(clave = valor, claveError = null, loginError = null) } }
    fun onNombreChange(valor: String) { _estado.update { it.copy(nombre = valor, nombreError = null) } }
    fun onTelefonoChange(valor: String) { _estado.update { it.copy(telefono = valor, telefonoError = null) } }
    fun onConfirmClaveChange(valor: String) { _estado.update { it.copy(confirmClave = valor, confirmClaveError = null) } }
    fun onAceptarTerminosChange(valor: Boolean) { _estado.update { it.copy(aceptaTerminos = valor) } }

    fun resetLoginStatus() {
        _loginExitoso.value = false
        _usuarioLogueado.value = null
        _estado.update { it.copy(loginError = null, loginLoading = false) }
    }

    fun resetAuthStatus() {
        _estado.value = UsuarioUiState()
    }

    fun intentarLogin() {
        _estado.update { it.copy(loginLoading = true, loginError = null) }

        if (_estado.value.correo.isBlank() || _estado.value.clave.isBlank()) {
            _estado.update { it.copy(loginError = "Complete todos los campos", loginLoading = false) }
            return
        }

        viewModelScope.launch {
            val usuario = usuarioRepository.autenticarUsuario(_estado.value.correo, _estado.value.clave)
            if (usuario != null) {
                _usuarioLogueado.value = usuario
                _loginExitoso.value = true
                _estado.update { it.copy(loginLoading = false) }
            } else {
                _estado.update { it.copy(loginLoading = false, loginError = "Credenciales incorrectas") }
            }
        }
    }

    fun intentarRegistro() {
        val nuevoUsuario = UsuarioEntity(
            nombre = _estado.value.nombre,
            email = _estado.value.correo,
            clave = _estado.value.clave,
            telefono = _estado.value.telefono
        )
        viewModelScope.launch {
            usuarioRepository.registrarUsuario(nuevoUsuario)
        }
    }

    class Factory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UsuarioViewModel(repository) as T
        }
    }
}