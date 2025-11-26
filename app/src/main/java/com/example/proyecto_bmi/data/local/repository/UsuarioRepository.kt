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
                telefono = usuario.telefono
            )
            val respuesta = RetrofitInstance.api.register(userParaNube)
            val usuarioConId = usuario.copy(id = respuesta.id ?: 0)
            return usuarioDao.insertUser(usuarioConId)
        } catch (e: Exception) {
            return usuarioDao.insertUser(usuario)
        }
    }

    suspend fun autenticarUsuario(email: String, clave: String): UsuarioEntity? {
        try {
            val loginData = UserRemote(nombre = "", email = email, password = clave, telefono = "")
            val respuestaBackend = RetrofitInstance.api.login(loginData)

            if (respuestaBackend != null) {
                val rolLocal = if (respuestaBackend.role == "ADMIN" || respuestaBackend.role == "PREMIUM") "Premium" else "Estandar"
                val usuarioValidado = UsuarioEntity(
                    id = respuestaBackend.id ?: 0,
                    nombre = respuestaBackend.nombre,
                    email = respuestaBackend.email,
                    clave = clave,
                    telefono = respuestaBackend.telefono,
                    tipoUsuario = rolLocal
                )
                usuarioDao.insertUser(usuarioValidado)
                return usuarioValidado
            }
        } catch (e: Exception) {
        }
        return usuarioDao.getUserByCredentials(email, clave)
    }

    suspend fun actualizarPerfil(usuario: UsuarioEntity): Boolean {
        return try {
            val userUpdate = UserRemote(
                id = usuario.id,
                nombre = usuario.nombre,
                email = usuario.email,
                password = usuario.clave,
                telefono = usuario.telefono,
                role = if (usuario.tipoUsuario == "Premium") "PREMIUM" else "USER"
            )
            RetrofitInstance.api.updateUser(usuario.id, userUpdate)
            usuarioDao.updateUser(usuario)
            true
        } catch (e: Exception) {
            usuarioDao.updateUser(usuario)
            false
        }
    }

    suspend fun obtenerUsuarioPorId(userId: Int): UsuarioEntity? {
        return usuarioDao.getUserById(userId)
    }

    suspend fun obtenerTodosLosUsuariosLocalmente(): List<UsuarioEntity> {
        return usuarioDao.getAllUsers()
    }

    suspend fun actualizarTipoUsuario(userId: Int, nuevoTipo: String) {
        usuarioDao.updateTipoUsuario(userId, nuevoTipo)
    }
}