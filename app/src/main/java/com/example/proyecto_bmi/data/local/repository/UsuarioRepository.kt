package com.example.proyecto_bmi.data.local.repository

import com.example.proyecto_bmi.data.local.dao.UsuarioDao
import com.example.proyecto_bmi.data.local.entity.UsuarioEntity

open class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun registrarUsuario(usuario: UsuarioEntity): Long {
        return usuarioDao.insertUser(usuario)
    }

    suspend fun autenticarUsuario(email: String, clave: String): UsuarioEntity? {
        return usuarioDao.getUserByCredentials(email, clave)
    }

    suspend fun obtenerUsuarioPorId(userId: Int): UsuarioEntity? {
        return usuarioDao.getUserById(userId)
    }

    suspend fun actualizarTipoUsuario(userId: Int, nuevoTipo: String) {
        usuarioDao.updateTipoUsuario(userId, nuevoTipo)
    }
}