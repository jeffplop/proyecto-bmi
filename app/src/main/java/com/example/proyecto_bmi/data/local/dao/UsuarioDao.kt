package com.example.proyecto_bmi.data.local.dao

import androidx.room.*
import com.example.proyecto_bmi.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UsuarioEntity): Long

    @Query("SELECT * FROM usuario WHERE email = :email AND clave = :clave LIMIT 1")
    suspend fun getUserByCredentials(email: String, clave: String): UsuarioEntity?

    @Query("SELECT * FROM usuario WHERE id = :userId")
    suspend fun getUserById(userId: Int): UsuarioEntity?

    @Query("UPDATE usuario SET tipoUsuario = :nuevoTipo WHERE id = :userId")
    suspend fun updateTipoUsuario(userId: Int, nuevoTipo: String)

    @Query("SELECT COUNT(*) FROM usuario")
    suspend fun count(): Int

    @Query("SELECT * FROM usuario")
    suspend fun getAllUsers(): List<UsuarioEntity>
}