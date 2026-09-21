package com.ctma.prestamoctma.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ctma.prestamoctma.data.local.entities.EvidenciaEntity
import com.ctma.prestamoctma.data.local.entities.UploadStatus
import kotlinx.coroutines.flow.Flow

@Suppress("unused")
@Dao
interface EvidenciaDao {

    @Query("SELECT * FROM evidencias WHERE prestamoId = :prestamoId")
    fun getEvidenciasByPrestamo(prestamoId: String): Flow<List<EvidenciaEntity>>

    @Query("SELECT * FROM evidencias")
    fun getAllEvidencias(): Flow<List<EvidenciaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidencia(evidencia: EvidenciaEntity)

    @Update
    suspend fun updateEvidencia(evidencia: EvidenciaEntity)

    @Query("UPDATE evidencias SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: UploadStatus)

    @Query("SELECT * FROM evidencias WHERE id = :id")
    suspend fun getEvidenciaById(id: String): EvidenciaEntity?

    @Query("SELECT * FROM evidencias WHERE status = 'LOCAL' OR status = 'FALLIDA'")
    suspend fun getPendingUploads(): List<EvidenciaEntity>
}
