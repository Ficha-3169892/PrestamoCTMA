package com.ctma.prestamoctma.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class UploadStatus {
    LOCAL, SUBIENDO, SINCRONIZADA, FALLIDA
}

@Entity(
    tableName = "evidencias",
    foreignKeys = [
        ForeignKey(
            entity = PrestamoEntity::class,
            parentColumns = ["id"],
            childColumns = ["prestamoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["prestamoId"])]
)
data class EvidenciaEntity(
    @PrimaryKey
    val id: String,
    val prestamoId: String,
    val localUri: String,
    val remoteUrl: String? = null,
    val status: UploadStatus = UploadStatus.LOCAL,
    val mimeType: String,
    val fileSizeBytes: Long
)
