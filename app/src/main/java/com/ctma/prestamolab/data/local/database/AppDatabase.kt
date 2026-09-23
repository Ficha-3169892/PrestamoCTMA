package com.ctma.prestamolab.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ctma.prestamolab.data.local.dao.EquipoDao
import com.ctma.prestamolab.data.local.dao.SolicitudDao
import com.ctma.prestamolab.data.local.dao.UsuarioDao
import com.ctma.prestamolab.data.local.entity.EquipoEntity
import com.ctma.prestamolab.data.local.entity.SolicitudEntity
import com.ctma.prestamolab.data.local.entity.UsuarioEntity

@Database(entities = [EquipoEntity::class, SolicitudEntity::class, UsuarioEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun equipoDao(): EquipoDao
    abstract fun solicitudDao(): SolicitudDao
    abstract fun usuarioDao(): UsuarioDao
}
