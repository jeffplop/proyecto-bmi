package com.example.proyecto_bmi.data.local.repository

import com.example.proyecto_bmi.data.local.dao.CatalogoDao
import com.example.proyecto_bmi.data.local.entity.CategoriaEntity
import com.example.proyecto_bmi.data.local.entity.FabricanteEntity
import com.example.proyecto_bmi.data.local.entity.FavoritosEntity
import com.example.proyecto_bmi.data.local.entity.ManualEntity
import kotlinx.coroutines.flow.Flow

class CatalogoRepository(private val catalogoDao: CatalogoDao) {

    fun obtenerTodosLosManuales(): Flow<List<ManualEntity>> {
        return catalogoDao.getAllManuales()
    }

    fun obtenerTodasLasCategorias(): Flow<List<CategoriaEntity>> {
        return catalogoDao.getAllCategorias()
    }

    suspend fun agregarManualAFavoritos(usuarioId: Int, manualId: Int) {
        val favorito = FavoritosEntity(usuarioId = usuarioId, manualId = manualId)
        catalogoDao.addFavorite(favorito)
    }

    suspend fun eliminarManualDeFavoritos(usuarioId: Int, manualId: Int) {
        val favorito = FavoritosEntity(usuarioId = usuarioId, manualId = manualId)
        catalogoDao.removeFavorite(favorito)
    }

    fun obtenerFavoritosDelUsuario(userId: Int): Flow<List<ManualEntity>> {
        return catalogoDao.getUserFavorites(userId)
    }

    // Función para precargar datos iniciales
    suspend fun precargarDatos(manuales: List<ManualEntity>, fabricantes: List<FabricanteEntity>, categorias: List<CategoriaEntity>) {
        catalogoDao.insertFabricantes(fabricantes)
        catalogoDao.insertCategorias(categorias)
        catalogoDao.insertManuales(manuales)
    }
}