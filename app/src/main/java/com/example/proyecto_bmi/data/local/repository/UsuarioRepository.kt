package com.example.proyecto_bmi.data.local.repository

import com.example.proyecto_bmi.data.local.dao.UsuarioDao
import com.example.proyecto_bmi.data.local.entity.UsuarioEntity
import com.example.proyecto_bmi.data.remote.api.RetrofitInstance
import com.example.proyecto_bmi.data.remote.model.UserRemote

open class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun registrarUsuario(usuario: UsuarioEntity): Long {
        try {
            val userParaNube = UserRemote(
                nombre = usuario.nombre,
                email = usuario.email,
                password = usuario.clave,
                direccion = usuario.direccion
            )
            RetrofitInstance.api.register(userParaNube)
            return usuarioDao.insertUser(usuario)
        } catch (e: Exception) {
            return usuarioDao.insertUser(usuario)
        }
    }

    suspend fun autenticarUsuario(email: String, clave: String): UsuarioEntity? {
        try {
            val loginData = UserRemote(nombre = "", email = email, password = clave, direccion = "")
            val respuestaBackend = RetrofitInstance.api.login(loginData)

            if (respuestaBackend != null) {
                val rolLocal = if (respuestaBackend.role == "ADMIN" || respuestaBackend.role == "PREMIUM") "Premium" else "Estandar"
                val usuarioValidado = UsuarioEntity(
                    nombre = respuestaBackend.nombre,
                    email = respuestaBackend.email,
                    clave = clave,
                    direccion = respuestaBackend.direccion,
                    tipoUsuario = rolLocal
                )
                usuarioDao.insertUser(usuarioValidado)
                return usuarioValidado
            }
        } catch (e: Exception) {
        }
        return usuarioDao.getUserByCredentials(email, clave)
    }

    suspend fun obtenerTodosLosUsuariosLocalmente(): List<UsuarioEntity> {
        return usuarioDao.getAllUsers()
    }

    suspend fun obtenerUsuarioPorId(userId: Int): UsuarioEntity? {
        return usuarioDao.getUserById(userId)
    }

    suspend fun actualizarTipoUsuario(userId: Int, nuevoTipo: String) {
        usuarioDao.updateTipoUsuario(userId, nuevoTipo)
    }
}