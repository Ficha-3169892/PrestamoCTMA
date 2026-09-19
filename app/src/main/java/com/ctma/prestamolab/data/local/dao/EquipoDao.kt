package com.ctma.prestamolab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ctma.prestamolab.data.local.entity.EquipoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {
    // [HU 01] Consulta de Catálogo de Equipos
    @Query("SELECT * FROM equipos")
    fun obtenerTodos(): Flow<List<EquipoEntity>>

    @Query("SELECT * FROM equipos WHERE nombre LIKE '%' || :query || '%' OR serie LIKE '%' || :query || '%'")
    fun buscar(query: String): Flow<List<EquipoEntity>>

    @Query("SELECT * FROM equipos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): EquipoEntity?

    // [HU 09] Registro de Nuevos Equipos en Inventario
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(equipo: EquipoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLista(equipos: List<EquipoEntity>)

    @Update
    suspend fun actualizar(equipo: EquipoEntity)

    // [HU 08] Marcado de Equipos Frecuentes
    @Query("UPDATE equipos SET esFavorito = NOT esFavorito WHERE id = :id")
    suspend fun conmutarFavorito(id: Int)
}
