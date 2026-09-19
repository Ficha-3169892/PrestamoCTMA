package com.ctma.prestamolab.data.local.database

import androidx.room.TypeConverter
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.model.EstadoSolicitud

class Converters {
    @TypeConverter
    fun fromCategoria(value: CategoriaEquipo) = value.name

    @TypeConverter
    fun toCategoria(value: String) = CategoriaEquipo.valueOf(value)

    @TypeConverter
    fun fromEstadoEquipo(value: EstadoEquipo) = value.name

    @TypeConverter
    fun toEstadoEquipo(value: String) = EstadoEquipo.valueOf(value)

    @TypeConverter
    fun fromEstadoSolicitud(value: EstadoSolicitud) = value.name

    @TypeConverter
    fun toEstadoSolicitud(value: String) = EstadoSolicitud.valueOf(value)
}
