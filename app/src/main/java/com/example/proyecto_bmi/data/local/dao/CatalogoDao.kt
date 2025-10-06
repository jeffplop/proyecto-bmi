package com.example.proyecto_bmi.data.local.dao

import androidx.room.*
import com.example.proyecto_bmi.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFabricantes(fabricantes: List<FabricanteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategorias(categorias: List<CategoriaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManuales(manuales: List<ManualEntity>)

    @Query("SELECT * FROM manual")
    fun getAllManuales(): Flow<List<ManualEntity>>

    @Query("SELECT * FROM categoria")
    fun getAllCategorias(): Flow<List<CategoriaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoritosEntity)

    @Delete
    suspend fun removeFavorite(favorite: FavoritosEntity)

    @Query("""
        SELECT manual.* FROM manual 
        INNER JOIN favoritos ON manual.id = favoritos.manualId 
        WHERE favoritos.usuarioId = :userId
    """)
    fun getUserFavorites(userId: Int): Flow<List<ManualEntity>>
}