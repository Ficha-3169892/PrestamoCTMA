package com.ctma.prestamolab.data

import com.ctma.prestamolab.PrestamoLabApplication
import com.ctma.prestamolab.data.local.database.AppDatabase
import com.ctma.prestamolab.data.remote.RemotePrestamoDataSource
import com.ctma.prestamolab.data.remote.supabase
import com.ctma.prestamolab.data.repository.AuthRepository
import com.ctma.prestamolab.data.repository.SupabaseAuthRepository
import com.ctma.prestamolab.data.repository.LocalPrestamoRepository
import com.ctma.prestamolab.data.repository.PrestamoRepository

/**
 * [HU General] Localizador de Servicios para Inyección de Dependencias manual.
 * Se encarga de proveer las instancias únicas de los repositorios (Semana 06).
 */
object ServiceLocator {
    
    private var database: AppDatabase? = null
    
    private val remoteDataSource by lazy { RemotePrestamoDataSource() }

    fun initialize(application: PrestamoLabApplication) {
        database = application.database
    }

    val repository: PrestamoRepository by lazy {
        val db = database ?: throw IllegalStateException("ServiceLocator no inicializado")
        LocalPrestamoRepository(db.equipoDao(), db.solicitudDao(), remoteDataSource)
    }
    
    val authRepository: AuthRepository by lazy {
        val db = database ?: throw IllegalStateException("ServiceLocator no inicializado")
        SupabaseAuthRepository(supabase, db.usuarioDao())
    }
}
