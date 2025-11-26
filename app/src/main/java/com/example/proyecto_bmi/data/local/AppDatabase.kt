package com.example.proyecto_bmi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyecto_bmi.data.local.dao.CatalogoDao
import com.example.proyecto_bmi.data.local.dao.UsuarioDao
import com.example.proyecto_bmi.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UsuarioEntity::class,
        FabricanteEntity::class,
        CategoriaEntity::class,
        ManualEntity::class,
        FavoritosEntity::class
    ],
    version = 2,
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
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).usuarioDao()

                                val seed = listOf(
                                    UsuarioEntity(
                                        nombre = "Admin",
                                        email = "admin@bmi.cl",
                                        clave = "Admin123!",
                                        telefono = "+56911111111",
                                        tipoUsuario = "Premium"
                                    ),
                                    UsuarioEntity(
                                        nombre = "Jeff Ploop",
                                        email = "jeff@bmi.cl",
                                        clave = "Jeff123!",
                                        telefono = "+56922222222"
                                    )
                                )

                                if (dao.count() == 0) {
                                    seed.forEach { dao.insertUser(it) }
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}