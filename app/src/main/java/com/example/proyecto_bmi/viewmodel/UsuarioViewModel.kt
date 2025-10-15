package com.example.proyecto_bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto_bmi.data.local.entity.UsuarioEntity
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import com.example.proyecto_bmi.domain.model.UsuarioErrores
import com.example.proyecto_bmi.domain.model.UsuarioUiState
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
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update {
            it.copy(
                correo = valor,
                errores = it.errores.copy(correo = null),
                loginError = null
            )
        }
        actualizarLoginStatus()
    }

    fun onClaveChange(valor: String) {
        _estado.update {
            it.copy(
                clave = valor,
                errores = it.errores.copy(clave = null, confirmClave = null),
                loginError = null
            )
        }
        actualizarLoginStatus()
    }

    fun onConfirmClaveChange(valor: String) {
        _estado.update { it.copy(confirmClave = valor, errores = it.errores.copy(confirmClave = null)) }
    }

    fun onDireccionChange(valor: String) {
        _estado.update { it.copy(direccion = valor, errores = it.errores.copy(direccion = null)) }
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

    private fun actualizarLoginStatus() {
        val estadoActual = _estado.value
        val emailValido = estadoActual.correo.contains("@") && estadoActual.correo.isNotBlank()
        val claveValida = estadoActual.clave.length >= 6

        _estado.update {
            it.copy(
                loginEnabled = emailValido && claveValida
            )
        }
    }

    fun intentarLogin() {
        _estado.update { it.copy(loginLoading = true, loginError = null) }

        val estadoActual = _estado.value

        if (!validarLogin()) {
            _estado.update {
                it.copy(
                    loginLoading = false,
                    loginError = "Por favor, revise el formato del correo y la longitud mínima de la contraseña."
                )
            }
            return
        }

        viewModelScope.launch {
            val usuario = usuarioRepository.autenticarUsuario(estadoActual.correo, estadoActual.clave)

            _estado.update { it.copy(loginLoading = false) }

            if (usuario != null) {
                _loginExitoso.value = true
                _estado.update { it.copy(nombre = usuario.nombre, correo = usuario.email) }
            } else {
                _estado.update { it.copy(loginError = "Credenciales incorrectas. Verifique su correo y contraseña.") }
            }
        }
    }

    private fun validarLogin(): Boolean {
        val estadoActual = _estado.value

        var erroresEncontrados = UsuarioErrores()

        if (!estadoActual.correo.contains("@") || estadoActual.correo.isBlank()) {
            erroresEncontrados = erroresEncontrados.copy(correo = "El formato del correo electrónico es inválido")
        }
        if (estadoActual.clave.length < 6) {
            erroresEncontrados = erroresEncontrados.copy(clave = "La contraseña debe contener al menos 6 caracteres")
        }

        _estado.update { it.copy(errores = erroresEncontrados) }

        val hayErrores = listOfNotNull(
            erroresEncontrados.correo,
            erroresEncontrados.clave,
        ).isNotEmpty()

        return !hayErrores
    }

    fun intentarRegistro(): Boolean {
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
            return true
        }
        return false
    }

    private fun validarFormulario(): Boolean {
        val estadoActual = _estado.value

        var erroresEncontrados = UsuarioErrores()

        if (estadoActual.nombre.isBlank()) erroresEncontrados = erroresEncontrados.copy(nombre = "Este campo es obligatorio")
        if (!estadoActual.correo.contains("@")) erroresEncontrados = erroresEncontrados.copy(correo = "El formato del correo electrónico es inválido")
        if (estadoActual.clave.length < 6) erroresEncontrados = erroresEncontrados.copy(clave = "La contraseña debe contener al menos 6 caracteres")
        if (estadoActual.direccion.isBlank()) erroresEncontrados = erroresEncontrados.copy(direccion = "Este campo es obligatorio")

        if (estadoActual.clave != estadoActual.confirmClave) erroresEncontrados = erroresEncontrados.copy(confirmClave = "La confirmación de la contraseña no coincide")

        val hayErrores = listOfNotNull(
            erroresEncontrados.nombre,
            erroresEncontrados.correo,
            erroresEncontrados.clave,
            erroresEncontrados.confirmClave,
            erroresEncontrados.direccion
        ).isNotEmpty()

        _estado.update { it.copy(errores = erroresEncontrados) }
        return !hayErrores
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