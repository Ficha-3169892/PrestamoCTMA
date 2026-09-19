package com.ctma.prestamolab

import android.app.Application
import androidx.room.Room
import com.ctma.prestamolab.data.ServiceLocator
import com.ctma.prestamolab.data.local.database.AppDatabase

/**
 * [HU General] Clase de aplicación para centralizar la inyección de dependencias
 * y la inicialización de Room (Semana 06).
 */
class PrestamoLabApplication : Application() {
    
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Inicialización de la Fuente Única de Verdad (SSOT)
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "prestamolab-db"
        ).build()

        // Inyectar la base de datos en el ServiceLocator
        ServiceLocator.initialize(this)
    }
}
