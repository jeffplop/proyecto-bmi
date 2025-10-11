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

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        // Al cambiar clave, limpiamos el error de confirmación para forzar re-validación
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null, confirmClave = null)) }
    }

    fun onConfirmClaveChange(valor: String) {
        // Mantenemos el campo de clave intacto y actualizamos el campo de confirmación
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

        if (estadoActual.nombre.isBlank()) erroresEncontrados = erroresEncontrados.copy(nombre = "Campo obligatorio")
        if (!estadoActual.correo.contains("@")) erroresEncontrados = erroresEncontrados.copy(correo = "Correo inválido")
        if (estadoActual.clave.length < 6) erroresEncontrados = erroresEncontrados.copy(clave = "Debe tener al menos 6 caracteres")
        if (estadoActual.direccion.isBlank()) erroresEncontrados = erroresEncontrados.copy(direccion = "Campo obligatorio")
        if (estadoActual.clave != estadoActual.confirmClave) erroresEncontrados = erroresEncontrados.copy(confirmClave = "Las contraseñas no coinciden")

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
