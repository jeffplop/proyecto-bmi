package com.example.proyecto_bmi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyecto_bmi.model.UsuarioErrores
import com.example.proyecto_bmi.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class UsuarioViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(value = UsuarioUiState())
    val uiState: StateFlow<UsuarioUiState> = _uiState

    fun onNombreChange(valor: String) {
        _uiState.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _uiState.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onDireccionChange(valor: String) {
        _uiState.update { it.copy(direccion = valor, errores = it.errores.copy(direccion = null)) }
    }

    fun onAceptarTerminosChange(valor: Boolean) {
        _uiState.update { it.copy(aceptaTerminos = valor) }
    }

    fun validarYRegistrar() {
        if (validarFormulario()) {
            _uiState.update { it.copy(isRegistered = true) }
        }
    }

    fun volverAFormulario() {
        _uiState.update { UsuarioUiState() }
    }

    private fun validarFormulario(): Boolean {
        val estadoActual = _uiState.value
        val errores = UsuarioErrores(
            nombre = if (estadoActual.nombre.isBlank()) "Campo obligatorio" else null,
            correo = if (!estadoActual.correo.contains("@") || estadoActual.correo.isBlank()) "Correo inválido" else null,
            clave = if (estadoActual.clave.length < 6) "Debe tener al menos 6 caracteres" else null,
            direccion = if (estadoActual.direccion.isBlank()) "Campo obligatorio" else null
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.correo,
            errores.clave,
            errores.direccion
        ).isNotEmpty()

        _uiState.update { it.copy(errores = errores) }

        return !hayErrores
    }
}