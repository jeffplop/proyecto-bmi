package com.example.proyecto_bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto_bmi.data.local.entity.UsuarioEntity
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import com.example.proyecto_bmi.domain.model.UsuarioUiState
import com.example.proyecto_bmi.domain.validation.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class UsuarioViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(value = UsuarioUiState())
    open val estado: StateFlow<UsuarioUiState> = _estado

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso

    private val _loginExitoso = MutableStateFlow(false)
    val loginExitoso: StateFlow<Boolean> = _loginExitoso

    fun onNombreChange(valor: String) {
        val error = validateNameLettersOnly(valor)
        _estado.update { it.copy(nombre = valor, nombreError = error) }
    }

    fun onCorreoChange(valor: String) {
        val error = validateEmail(valor)
        _estado.update {
            it.copy(
                correo = valor,
                correoError = error,
                loginError = null
            )
        }
    }

    fun onClaveChange(valor: String) {
        val error = validateStrongPassword(valor)
        _estado.update {
            it.copy(
                clave = valor,
                claveError = error,
                loginError = null
            )
        }
    }

    fun onConfirmClaveChange(valor: String) {
        val error = validateConfirm(_estado.value.clave, valor)
        _estado.update { it.copy(confirmClave = valor, confirmClaveError = error) }
    }

    fun onDireccionChange(valor: String) {
        val error = validateIsNotBlank(valor, "Dirección")
        _estado.update { it.copy(direccion = valor, direccionError = error) }
    }

    fun onAceptarTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor) }
    }

    fun resetAuthStatus() {
        _registroExitoso.value = false
        _estado.value = UsuarioUiState()
    }

    fun resetLoginStatus() {
        _loginExitoso.value = false
        _estado.update {
            it.copy(
                correo = "",
                clave = "",
                loginError = null,
                loginLoading = false
            )
        }
    }

    fun intentarLogin() {
        _estado.update { it.copy(loginLoading = true, loginError = null) }

        val emailError = validateEmail(_estado.value.correo)
        val claveError = validateIsNotBlank(_estado.value.clave, "Contraseña")

        if (emailError != null || claveError != null) {
            _estado.update {
                it.copy(
                    correoError = emailError,
                    claveError = claveError,
                    loginLoading = false
                )
            }
            return
        }

        viewModelScope.launch {
            val usuario = usuarioRepository.autenticarUsuario(_estado.value.correo, _estado.value.clave)

            if (usuario != null) {
                _loginExitoso.value = true
                _estado.update {
                    it.copy(
                        loginLoading = false,
                        nombre = usuario.nombre,
                        correo = usuario.email,
                        direccion = usuario.direccion
                    )
                }
            } else {
                _estado.update {
                    it.copy(
                        loginLoading = false,
                        loginError = "Credenciales incorrectas."
                    )
                }
            }
        }
    }

    fun intentarRegistro() {
        if (validarFormulario()) {
            val estadoActual = _estado.value
            val nuevoUsuario = UsuarioEntity(
                nombre = estadoActual.nombre,
                email = estadoActual.correo,
                clave = estadoActual.clave,
                direccion = estadoActual.direccion
            )

            viewModelScope.launch {
                val idUsuario = usuarioRepository.registrarUsuario(nuevoUsuario)
                if (idUsuario > 0) {
                    _registroExitoso.value = true
                }
            }
        }
    }

    private fun validarFormulario(): Boolean {
        val estadoActual = _estado.value

        val nombreError = validateNameLettersOnly(estadoActual.nombre)
        val correoError = validateEmail(estadoActual.correo)
        val claveError = validateStrongPassword(estadoActual.clave)
        val confirmClaveError = validateConfirm(estadoActual.clave, estadoActual.confirmClave)
        val direccionError = validateIsNotBlank(estadoActual.direccion, "Dirección")

        val hayErrores = listOfNotNull(
            nombreError,
            correoError,
            claveError,
            confirmClaveError,
            direccionError
        ).isNotEmpty()

        _estado.update {
            it.copy(
                nombreError = nombreError,
                correoError = correoError,
                claveError = claveError,
                confirmClaveError = confirmClaveError,
                direccionError = direccionError
            )
        }
        return !hayErrores && estadoActual.aceptaTerminos
    }

    class Factory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
                return UsuarioViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}