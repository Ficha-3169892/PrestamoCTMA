package com.ctma.prestamolab

import android.app.Application
import androidx.room.Room
import com.ctma.prestamolab.data.ServiceLocator
import com.ctma.prestamolab.data.local.database.AppDatabase
import com.ctma.prestamolab.BuildConfig
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.EstadoEquipo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
            BuildConfig.DB_NAME
        )
        .fallbackToDestructiveMigration()
        .build()

        // Inyectar la base de datos en el ServiceLocator
        ServiceLocator.initialize(this)

        // [Semana 06 & 08] Sincronización Inicial Automatizada si SSOT está vacía
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val equipoDao = database.equipoDao()
                val equipos = equipoDao.obtenerTodos().first()
                if (equipos.isEmpty()) {
                    // [HU-01] Datos semilla iniciales
                    val semillas = listOf(
                        Equipo(0, "Multímetro Digital Fluke 115", "SN-MED-001", "Fluke", CategoriaEquipo.MEDICION, EstadoEquipo.DISPONIBLE, "Precisión profesional.", listOf("Cables"), "Estante A1", null, true),
                        Equipo(0, "Analizador de Redes Cisco Pro", "SN-RED-045", "Cisco", CategoriaEquipo.REDES, EstadoEquipo.DISPONIBLE, "Diagnóstico de red.", listOf("Cable consola"), "Lab 2", null, false),
                        Equipo(0, "Kit de Soldadura Pro Goot", "SN-ELE-882", "Goot", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE, "Alta temperatura.", listOf("Base"), "Armario E", null, true),
                        Equipo(0, "Osciloscopio Digital Rigol", "SN-MED-550", "Rigol", CategoriaEquipo.MEDICION, EstadoEquipo.DISPONIBLE, "100MHz 2 canales.", listOf("Sondas"), "Estante B3", null, false),
                        Equipo(0, "Ponchadora de Impacto", "SN-HER-009", "Klein Tools", CategoriaEquipo.HERRAMIENTA, EstadoEquipo.DISPONIBLE, "Uso pesado.", listOf("Cuchilla"), "Maletín 1", null, true)
                    )

                    // Insertar a través del repositorio para activar la sincronización cloud automáticamente
                    semillas.forEach { domain ->
                        ServiceLocator.repository.agregarEquipo(domain)
                    }
                }
            } catch (e: Exception) {
                // Manejar errores silenciosamente en inicialización
            }
        }
    }
}
