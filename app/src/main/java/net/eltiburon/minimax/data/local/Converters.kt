package net.eltiburon.minimax.data.local

import androidx.room.TypeConverter
import net.eltiburon.minimax.model.EstadoCompra
import net.eltiburon.minimax.model.EstadoGrupo

/** Los enums del dominio se guardan como su nombre (String) en SQLite. */
class Converters {

    @TypeConverter
    fun estadoGrupoToString(estado: EstadoGrupo): String = estado.name

    @TypeConverter
    fun stringToEstadoGrupo(value: String): EstadoGrupo = EstadoGrupo.valueOf(value)

    @TypeConverter
    fun estadoCompraToString(estado: EstadoCompra): String = estado.name

    @TypeConverter
    fun stringToEstadoCompra(value: String): EstadoCompra = EstadoCompra.valueOf(value)
}
