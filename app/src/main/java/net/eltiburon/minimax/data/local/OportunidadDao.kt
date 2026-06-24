package net.eltiburon.minimax.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OportunidadDao {

    @Query("SELECT * FROM oportunidades")
    fun obtenerTodas(): Flow<List<OportunidadEntity>>

    @Query("SELECT * FROM oportunidades WHERE id = :id")
    suspend fun obtenerPorId(id: String): OportunidadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(oportunidad: OportunidadEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodas(oportunidades: List<OportunidadEntity>)

    @Update
    suspend fun actualizar(oportunidad: OportunidadEntity)

    @Query("DELETE FROM oportunidades WHERE id = :id")
    suspend fun eliminar(id: String)

    @Query("SELECT COUNT(*) FROM oportunidades")
    suspend fun contar(): Int
}
