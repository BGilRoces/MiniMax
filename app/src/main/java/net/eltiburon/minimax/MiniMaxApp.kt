package net.eltiburon.minimax

import android.app.Application
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.eltiburon.minimax.data.OportunidadRepository
import net.eltiburon.minimax.data.ParticipacionRepository
import net.eltiburon.minimax.data.local.MiniMaxDatabase

/**
 * Construye la base de Room una sola vez y conecta los repositorios con sus DAOs.
 * Antes los repos eran MutableStateFlow puro en memoria; ahora persisten en SQLite,
 * pero siguen siendo singletons que las pantallas consumen igual que antes.
 */
class MiniMaxApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        val database = Room.databaseBuilder(
            applicationContext,
            MiniMaxDatabase::class.java,
            "minimax.db"
        ).build()

        OportunidadRepository.init(database.oportunidadDao())
        ParticipacionRepository.init(database.participacionDao())

        // Primera instalación: si la base está vacía, la precargamos con el catálogo demo
        // (antes vivía hardcodeado en los MutableStateFlow de cada repo).
        applicationScope.launch {
            OportunidadRepository.sembrarSiEstaVacia()
            ParticipacionRepository.sembrarSiEstaVacia()
        }
    }
}
