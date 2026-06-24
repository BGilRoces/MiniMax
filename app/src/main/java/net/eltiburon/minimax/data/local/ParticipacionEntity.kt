package net.eltiburon.minimax.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import net.eltiburon.minimax.model.EstadoCompra

/** Espejo de [net.eltiburon.minimax.model.Participacion] para persistencia con Room. */
@Entity(tableName = "participaciones")
data class ParticipacionEntity(
    @PrimaryKey val id: String,
    val oportunidadId: String,
    val usuarioEmail: String,
    val cantidad: Int,
    val fechaMillis: Long,
    val estado: EstadoCompra
)
