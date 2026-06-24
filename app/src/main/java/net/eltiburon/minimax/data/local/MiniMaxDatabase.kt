package net.eltiburon.minimax.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [OportunidadEntity::class, ParticipacionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MiniMaxDatabase : RoomDatabase() {
    abstract fun oportunidadDao(): OportunidadDao
    abstract fun participacionDao(): ParticipacionDao
}
