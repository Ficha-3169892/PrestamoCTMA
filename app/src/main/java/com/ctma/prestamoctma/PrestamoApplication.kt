package com.ctma.prestamoctma

import android.app.Application
import android.content.Context
import com.ctma.prestamoctma.data.local.AppDatabase
import com.ctma.prestamoctma.data.local.datastore.PreferenciasRepository
import com.ctma.prestamoctma.data.repository.OfflinePrestamoRepository
import com.ctma.prestamoctma.data.repository.PrestamoRepository

class PrestamoApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}

interface AppContainer {
    val prestamoRepository: PrestamoRepository
    val preferenciasRepository: PreferenciasRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val prestamoRepository: PrestamoRepository by lazy {
        val db = AppDatabase.getDatabase(context)
        OfflinePrestamoRepository(context, db.prestamoDao(), db.evidenciaDao())
    }

    override val preferenciasRepository: PreferenciasRepository by lazy {
        PreferenciasRepository(context)
    }
}
