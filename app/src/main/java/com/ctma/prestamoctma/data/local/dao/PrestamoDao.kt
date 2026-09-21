package com.ctma.prestamoctma.data.local.dao

import androidx.room.*
import com.ctma.prestamoctma.data.local.entities.ArticuloEntity
import com.ctma.prestamoctma.data.local.entities.PrestamoEntity
import kotlinx.coroutines.flow.Flow

@Suppress("unused")
@Dao
interface PrestamoDao {

    // Articulos
    @Query("SELECT * FROM articulos")
    fun getAllArticulos(): Flow<List<ArticuloEntity>>

    @Query("SELECT * FROM articulos WHERE id = :id")
    suspend fun getArticuloById(id: String): ArticuloEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticulos(articulos: List<ArticuloEntity>)

    @Update
    suspend fun updateArticulo(articulo: ArticuloEntity)

    // Prestamos
    @Query("SELECT * FROM prestamos ORDER BY fechaSolicitud DESC")
    fun getAllPrestamos(): Flow<List<PrestamoEntity>>

    @Query("SELECT * FROM prestamos WHERE usuarioId = :usuarioId ORDER BY fechaSolicitud DESC")
    fun getPrestamosByUsuario(usuarioId: String): Flow<List<PrestamoEntity>>

    @Query("SELECT * FROM prestamos WHERE id = :id")
    suspend fun getPrestamoById(id: String): PrestamoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrestamo(prestamo: PrestamoEntity)

    @Update
    suspend fun updatePrestamo(prestamo: PrestamoEntity)

    @Delete
    suspend fun deletePrestamo(prestamo: PrestamoEntity)

    @Transaction
    @Query("SELECT * FROM articulos WHERE id = :articuloId")
    suspend fun getArticuloConPrestamos(articuloId: String): ArticuloConPrestamos?
}

data class ArticuloConPrestamos(
    @Embedded val articulo: ArticuloEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "equipoId",
    )
    val prestamos: List<PrestamoEntity>
)
