package com.example.proyecto_bmi.data.local.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyecto_bmi.data.local.entity.*

@Database(
    entities = [
        UsuarioEntity::class,
        FabricanteEntity::class,
        CategoriaEntity::class,
        ManualEntity::class,
        FavoritosEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun catalogoDao(): CatalogoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "proyecto_bmi_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}