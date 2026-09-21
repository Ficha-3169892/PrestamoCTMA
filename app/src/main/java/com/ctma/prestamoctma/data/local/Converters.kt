package com.ctma.prestamoctma.data.local

import androidx.room.TypeConverter
import com.ctma.prestamoctma.data.local.entities.UploadStatus
import java.util.Date

@Suppress("unused")
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromUploadStatus(status: UploadStatus): String {
        return status.name
    }

    @TypeConverter
    fun toUploadStatus(value: String): UploadStatus {
        return UploadStatus.valueOf(value)
    }
}
