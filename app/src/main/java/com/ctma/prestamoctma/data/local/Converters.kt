package com.ctma.prestamoctma.data.local

import androidx.room.TypeConverter
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
}
