package com.ctma.prestamolab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ctma.prestamolab.data.local.entity.SolicitudEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDao {
    // [HU 03] Gestión y Cancelación de Solicitudes
    @Query("SELECT * FROM solicitudes ORDER BY fechaCreacion DESC")
    fun obtenerTodas(): Flow<List<SolicitudEntity>>

    @Query("SELECT * FROM solicitudes WHERE usuarioId = :usuarioId ORDER BY fechaCreacion DESC")
    fun obtenerPorUsuario(usuarioId: Int): Flow<List<SolicitudEntity>>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    suspend fun obtenerPorId(id: Int): SolicitudEntity?

    // [HU 02] Registrar Solicitud de Préstamo
    @Insert
    suspend fun insertar(solicitud: SolicitudEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLista(solicitudes: List<SolicitudEntity>)

    // [HU 11] Aprobación y Rechazo de Solicitudes
    @Update
    suspend fun actualizar(solicitud: SolicitudEntity)

    // [HU 16] Historial de Trazabilidad y Auditoría de Equipos
    @Query("SELECT * FROM solicitudes WHERE equipoId = :equipoId ORDER BY fechaCreacion DESC")
    fun obtenerPorEquipo(equipoId: Int): Flow<List<SolicitudEntity>>
}
