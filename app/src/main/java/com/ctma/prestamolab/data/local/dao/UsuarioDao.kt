package com.ctma.prestamolab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ctma.prestamolab.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerPorId(id: Int): UsuarioEntity?

    @Query("SELECT * FROM usuarios")
    fun obtenerTodos(): Flow<List<UsuarioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: UsuarioEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLista(usuarios: List<UsuarioEntity>)

    @Query("DELETE FROM usuarios")
    suspend fun borrarTodos()
}
