package com.ctma.prestamoctma.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ctma.prestamoctma.data.local.dao.EvidenciaDao
import com.ctma.prestamoctma.data.local.dao.PrestamoDao
import com.ctma.prestamoctma.data.local.entities.ArticuloEntity
import com.ctma.prestamoctma.data.local.entities.EvidenciaEntity
import com.ctma.prestamoctma.data.local.entities.PrestamoEntity

@Database(
    entities = [ArticuloEntity::class, PrestamoEntity::class, EvidenciaEntity::class],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    @Suppress("unused")
    abstract fun prestamoDao(): PrestamoDao
    
    @Suppress("unused")
    abstract fun evidenciaDao(): EvidenciaDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        @Suppress("unused")
        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "prestamo_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
