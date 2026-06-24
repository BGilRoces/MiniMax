package net.eltiburon.minimax.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ParticipacionDao {

    @Query("SELECT * FROM participaciones")
    fun obtenerTodas(): Flow<List<ParticipacionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(participacion: ParticipacionEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodas(participaciones: List<ParticipacionEntity>)

    @Query("UPDATE participaciones SET estado = 'CANCELADA' WHERE id = :id")
    suspend fun cancelar(id: String)

    @Query("SELECT COUNT(*) FROM participaciones")
    suspend fun contar(): Int

    @Query(
        "UPDATE oportunidades SET unidadesFaltantes = :unidadesFaltantes, progresoActual = :progresoActual " +
            "WHERE id = :oportunidadId"
    )
    suspend fun descontarStock(oportunidadId: String, unidadesFaltantes: Int, progresoActual: Int)

    /**
     * Inserta la participación y descuenta el stock de la oportunidad en una sola transacción:
     * o se hacen las dos cosas, o ninguna. Room permite mezclar tablas dentro de un mismo Dao,
     * así que "oportunidades" se actualiza acá aunque viva en OportunidadEntity.
     */
    @Transaction
    suspend fun confirmarParticipacion(
        participacion: ParticipacionEntity,
        oportunidadId: String,
        unidadesFaltantesNuevas: Int,
        progresoActualNuevo: Int
    ) {
        insertar(participacion)
        descontarStock(oportunidadId, unidadesFaltantesNuevas, progresoActualNuevo)
    }
}
